package com.jsoniter.dson;

import com.jsoniter.dson.any.Any;
import com.jsoniter.dson.any.AnyList;
import com.jsoniter.dson.any.AnyMap;
import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.decode.BytesDecoderSource;
import com.jsoniter.dson.decode.DsonDecodeException;
import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesEncoderSink;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;
import com.jsoniter.dson.spi.DsonSpi;
import com.jsoniter.dson.spi.Encoder;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Dson implements DsonSpi {

    public static Dson $ = new Dson();

    public static class Config extends Codegen.Config {
        public BiFunction<DsonSpi, Type, Decoder> chooseDecoder;
        public BiFunction<DsonSpi, Class, Encoder> chooseEncoder;
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
        put(Map.class, new MapEncoder(Dson.this::encoderOf));
        put(Iterable.class, new IterableEncoder());
    }};
    private final Map<Type, Decoder> builtinDecoders = new HashMap<Type, Decoder>() {{
        put(boolean.class, source -> source.decodeNull() ? false : source.decodeBoolean());
        put(Boolean.class, source -> source.decodeNull() ? null : source.decodeBoolean());
        put(byte.class, source -> source.decodeNull() ? (byte)0 : (short)source.decodeInt());
        put(Byte.class, source -> source.decodeNull() ? null : (short)source.decodeInt());
        put(short.class, source -> source.decodeNull() ? (short)0 : (short)source.decodeInt());
        put(Short.class, source -> source.decodeNull() ? null : (short)source.decodeInt());
        put(int.class, source -> source.decodeNull() ? 0 : source.decodeInt());
        put(Integer.class, source -> source.decodeNull() ? null : source.decodeInt());
        put(long.class, source -> source.decodeNull() ? 0L : source.decodeLong());
        put(Long.class, source -> source.decodeNull() ? null : source.decodeLong());
        put(float.class, source -> source.decodeNull() ? 0.0F : (float)source.decodeDouble());
        put(Float.class, source -> source.decodeNull() ? null : (float)source.decodeDouble());
        put(double.class, source -> source.decodeNull() ? 0.0D : source.decodeDouble());
        put(Double.class, source -> source.decodeNull() ? null : source.decodeDouble());
        put(String.class, source -> source.decodeNull() ? null : source.decodeString());
        put(byte[].class, source -> source.decodeNull() ? null : source.decodeBytes());
        put(Byte[].class, source -> {
            if (source.decodeNull()) {
                return null;
            }
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

    public Dson(Config cfg) {
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
        codegen = new Codegen(cfg, this);
    }

    public Dson() {
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
            ObjectDecoder objectDecoder = new ObjectDecoder();
            decoderCache.put(Object.class, objectDecoder);
            objectDecoder.init(
                    decoderOf(cfg.chooseImpl.apply(List.class)),
                    decoderOf(cfg.chooseImpl.apply(Map.class)));
            return objectDecoder;
        }
        if (Any.class.equals(type)) {
            return new AnyDecoder(decoderOf(Object.class));
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

    public Any decode(String encoded) {
        return decode(encoded.getBytes(StandardCharsets.UTF_8));
    }

    public Any decode(byte[] encoded) {
        return decode(Any.class, encoded);
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
        return (T) decode(typeLiteral.$(), encoded, offset, size);
    }

    private Object decode(Type type, byte[] encoded, int offset, int size) {
        BytesDecoderSource source = new BytesDecoderSource(this::decoderOf, encoded, offset, size);
        return source.decodeObject(type);
    }
}
