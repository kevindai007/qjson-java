package org.qjson;

import org.qjson.any.Any;
import org.qjson.any.AnyList;
import org.qjson.any.AnyMap;
import org.qjson.codegen.Codegen;
import org.qjson.decode.BytesDecoderSource;
import org.qjson.decode.QJsonDecodeException;
import org.qjson.encode.BytesBuilder;
import org.qjson.encode.BytesEncoderSink;
import org.qjson.encode.StringEncoderSink;
import org.qjson.spi.Decoder;
import org.qjson.spi.QJsonSpi;
import org.qjson.spi.Encoder;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class QJSON implements QJsonSpi {

    public static QJSON $ = new QJSON();

    public static class Config extends Codegen.Config {
        public BiFunction<QJsonSpi, Type, Decoder> chooseDecoder;
        public BiFunction<QJsonSpi, Class, Encoder> chooseEncoder;
    }

    private final Map<Class, Encoder> builtinEncoders = BuiltinEncoders.$(this);
    private final Map<Type, Decoder> builtinDecoders = BuiltinDecoders.$();
    private final Map<Class, Encoder> encoderCache = new ConcurrentHashMap<>();
    private final Map<Type, Decoder> decoderCache = new ConcurrentHashMap<>();
    private final Config cfg;
    private final Codegen codegen;

    public QJSON(Config cfg) {
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
                throw new QJsonDecodeException("can not determine implementation class to decode: " + clazz);
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

    public QJSON() {
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
        StringEncoderSink sink = new StringEncoderSink(this::encoderOf, new StringBuilder());
        sink.encodeObject(val);
        return sink.toString();
    }

    public String encode(Object val, StringBuilder builder) {
        builder.setLength(0);
        StringEncoderSink sink = new StringEncoderSink(this::encoderOf, builder);
        sink.encodeObject(val);
        return sink.toString();
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
        return (T) decode(typeLiteral.$(), encoded, offset, size);
    }

    private Object decode(Type type, byte[] encoded, int offset, int size) {
        BytesDecoderSource source = new BytesDecoderSource(this::decoderOf, encoded, offset, size);
        return source.decodeObject(type);
    }

    // === static api ===

    public static Any parse(String encoded) {
        return $.decode(Any.class, encoded);
    }

    public static Any parse(byte[] encoded) {
        return $.decode(Any.class, encoded);
    }

    public static String stringify(Object val) {
        return $.encode(val);
    }
}
