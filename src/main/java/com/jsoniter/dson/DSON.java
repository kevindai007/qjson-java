package com.jsoniter.dson;

import com.jsoniter.dson.any.AnyList;
import com.jsoniter.dson.any.AnyMap;
import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.decode.BytesDecoderSource;
import com.jsoniter.dson.decode.DsonDecodeException;
import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesEncoderSink;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;
import com.jsoniter.dson.spi.Encoder;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DSON {

    public static class Config extends Codegen.Config {
        public BiFunction<DSON, Type, Decoder> decoderProvider;
        public BiFunction<DSON, Class, Encoder> encoderProvider;
    }

    private final Map<Class, Encoder> builtinEncoders = new HashMap<Class, Encoder>() {{
        put(Boolean.class, (sink, val) -> sink.encodeBoolean((Boolean) val));
        put(Byte.class, (sink, val) -> sink.encodeInt((Byte) val));
        put(Short.class, (sink, val) -> sink.encodeInt((Short) val));
        put(Integer.class, (sink, val) -> sink.encodeInt((Integer) val));
        put(Long.class, (sink, val) -> sink.encodeLong((Long) val));
        put(Character.class, (sink, val) -> sink.encodeString(new String(new char[]{(Character) val})));
        put(String.class, (sink, val) -> sink.encodeString((String) val));
        put(Float.class, (sink, val) -> sink.encodeDouble((Float) val));
        put(Double.class, (sink, val) -> sink.encodeDouble((Double) val));
        put(byte[].class, (sink, val) -> sink.encodeBytes((byte[]) val));
        put(Byte[].class, (sink, val) -> {
            Byte[] boxed = (Byte[]) val;
            byte[] bytes = new byte[boxed.length];
            for (int i = 0; i < boxed.length; i++) {
                bytes[i] = boxed[i];
            }
            sink.encodeBytes(bytes);
        });
        put(Map.class, new MapEncoder(DSON.this::encoderOf));
        put(Iterable.class, new IterableEncoder());
    }};
    private final Map<Type, Decoder> builtinDecoders = new HashMap<Type, Decoder>() {{
        put(boolean.class, DecoderSource::decodeBoolean);
        put(Boolean.class, DecoderSource::decodeBoolean);
        put(byte.class, source -> (byte)source.decodeInt());
        put(Byte.class, source -> (byte)source.decodeInt());
        put(short.class, source -> (short)source.decodeInt());
        put(Short.class, source -> (short)source.decodeInt());
        put(int.class, DecoderSource::decodeInt);
        put(Integer.class, DecoderSource::decodeInt);
        put(long.class, DecoderSource::decodeLong);
        put(Long.class, DecoderSource::decodeLong);
        put(float.class, source -> (float)source.decodeDouble());
        put(Float.class, source -> (float)source.decodeDouble());
        put(double.class, DecoderSource::decodeDouble);
        put(Double.class, DecoderSource::decodeDouble);
        put(String.class, DecoderSource::decodeString);
        put(byte[].class, DecoderSource::decodeBytes);
        put(Byte[].class, source -> {
            byte[] bytes = source.decodeBytes();
            Byte[] boxed = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                boxed[i] = bytes[i];
            }
            return boxed;
        });
    }};
    private final Map<Class, Encoder> encoderCache = new ConcurrentHashMap<>();
    private final Map<Type, Decoder> decoderCache = new ConcurrentHashMap<>();
    private final Config cfg;
    private final Codegen codegen;

    public DSON(Config cfg) {
        if (cfg.compiler == null) {
            cfg.compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
        }
        Map<Class, Class> implMap = new HashMap<Class, Class>() {{
            put(Map.class, AnyMap.class);
            put(Iterable.class, AnyList.class);
            put(Collection.class, AnyList.class);
            put(List.class, AnyList.class);
            put(Set.class, HashSet.class);
        }};
        Function<Class, Class> defaultChooseImpl = clazz -> {
            if (clazz.isPrimitive() || clazz.isArray()) {
                return clazz;
            }
            if (!(Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface())) {
                return clazz;
            }
            Class impl = implMap.get(clazz);
            if (impl == null) {
                throw new DsonDecodeException("can not determine implementation class to decode: " + clazz);
            }
            return impl;
        };
        if (cfg.chooseImpl == null) {
            cfg.chooseImpl = defaultChooseImpl;
        } else {
            Function<Class, Class> userChooseImpl = cfg.chooseImpl;
            cfg.chooseImpl = clazz -> {
                Class impl = userChooseImpl.apply(clazz);
                if (impl != null) {
                    return impl;
                }
                return defaultChooseImpl.apply(clazz);
            };
        }
        this.cfg = cfg;
        codegen = new Codegen(cfg, this::decoderOf);
    }

    public DSON() {
        this(new Config());
    }

    public Encoder encoderOf(Class clazz) {
        return encoderCache.computeIfAbsent(clazz, this::generateEncoder);
    }

    private Encoder generateEncoder(Class clazz) {
        Encoder encoder = builtinEncoders.get(clazz);
        if (encoder != null) {
            return encoder;
        }
        boolean isJavaUtil = Codegen.isJavaUtil(clazz);
        if (Map.class.isAssignableFrom(clazz) && isJavaUtil) {
            return builtinEncoders.get(Map.class);
        }
        if (Iterable.class.isAssignableFrom(clazz) && isJavaUtil) {
            return builtinEncoders.get(Iterable.class);
        }
        return codegen.generateEncoder(clazz);
    }

    public Decoder decoderOf(Type type) {
        Decoder decoder = decoderCache.get(type);
        if (decoder == null) {
            // placeholder to avoid infinite loop
            decoderCache.put(type, source -> decoderOf(type).decode(source));
            decoder = generateDecoder(type);
            decoderCache.put(type, decoder);
        }
        return decoder;
    }

    private Decoder generateDecoder(Type type) {
        Decoder decoder = builtinDecoders.get(type);
        if (decoder != null) {
            return decoder;
        }
        if (Object.class.equals(type)) {
            return new ObjectDecoder(
                    decoderOf(cfg.chooseImpl.apply(List.class)),
                    decoderOf(cfg.chooseImpl.apply(Map.class)));
        }
        return codegen.generateDecoder(type);
    }

    public String encode(Object val) {
        BytesBuilder builder = new BytesBuilder();
        encode(val, builder);
        return builder.toString();
    }

    public void encode(Object val, BytesBuilder bytesBuilder) {
        BytesEncoderSink sink = new BytesEncoderSink(this::encoderOf, bytesBuilder);
        sink.encodeObject(val);
    }

    public <T> T decode(Class<T> clazz, String encoded) {
        return decode(clazz, encoded.getBytes(StandardCharsets.UTF_8));
    }

    public <T> T decode(Class<T> clazz, byte[] encoded) {
        return decode(clazz, encoded, 0, encoded.length);
    }

    public <T> T decode(Class<T> clazz, byte[] encoded, int offset, int size) {
        return (T) decode((Type) clazz, encoded, offset, size);
    }

    public <T> T decode(TypeLiteral<T> typeLiteral, byte[] encoded, int offset, int size) {
        if (TypeLiteral.class.equals(typeLiteral.getClass())) {
            throw new DsonDecodeException("should specify type like this: new TypeLiteral<List<String>>(){}");
        }
        ParameterizedType parameterizedType = (ParameterizedType) typeLiteral.getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        return (T) decode(type, encoded, offset, size);
    }

    private Object decode(Type type, byte[] encoded, int offset, int size) {
        BytesDecoderSource source = new BytesDecoderSource(this::decoderOf, encoded, offset, size);
        return source.decodeObject(type);
    }
}
