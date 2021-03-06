package org.qjson;

import org.mdkt.compiler.InMemoryJavaCompiler;
import org.qjson.any.Any;
import org.qjson.any.AnyList;
import org.qjson.any.AnyMap;
import org.qjson.codegen.Codegen;
import org.qjson.decode.BytesDecoderSource;
import org.qjson.decode.QJsonDecodeException;
import org.qjson.decode.StringDecoderSource;
import org.qjson.encode.BytesBuilder;
import org.qjson.encode.BytesEncoderSink;
import org.qjson.encode.StringEncoderSink;
import org.qjson.spi.*;

import java.lang.reflect.*;
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
        if (cfg.chooseEncoder == null) {
            cfg.chooseEncoder = (qjson, clazz) -> null;
        }
        if (cfg.chooseDecoder == null) {
            cfg.chooseDecoder = (qjson, clazz) -> null;
        }
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

    @Override
    public Function<DecoderSource, Object> factoryOf(Class clazz) {
        clazz = cfg.chooseImpl.apply(clazz);
        if (clazz.equals(AnyMap.class)) {
            return source -> new AnyMap();
        }
        if (clazz.equals(HashMap.class)) {
            return source -> new HashMap();
        }
        if (clazz.equals(AnyList.class)) {
            return source -> new AnyList();
        }
        if (clazz.equals(ArrayList.class)) {
            return source -> new ArrayList();
        }
        try {
            Constructor ctor = clazz.getConstructor();
            return source -> {
                try {
                    return (Map) ctor.newInstance();
                } catch (Exception e) {
                    throw source.reportError("create map failed", e);
                }
            };

        } catch (NoSuchMethodException e) {
            throw new QJsonDecodeException("no default constructor for: " + clazz, e);
        }
    }

    public Encoder encoderOf(Class clazz) {
        return encoderCache.computeIfAbsent(clazz, this::generateEncoder);
    }

    private Encoder generateEncoder(Class clazz) {
        Encoder encoder = cfg.chooseEncoder.apply(this, clazz);
        if (encoder != null) {
            return encoder;
        }
        encoder = builtinEncoders.get(clazz);
        if (encoder != null) {
            return encoder;
        }
        boolean isJavaUtil = isJavaUtil(clazz);
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
            decoderCache.put(type, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return decoderOf(type).decode(source);
                }

                @Override
                public void decodeProperties(DecoderSource source, Object obj) {
                    decoderOf(type).decodeProperties(source, obj);
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return decoderOf(type).decodeNull(source);
                }

                @Override
                public Object decodeRef(DecoderSource source, String path, Object ref) {
                    return decoderOf(type).decodeRef(source, path, ref);
                }
            });
            decoder = generateDecoder(type);
            decoderCache.put(type, decoder);
        }
        return decoder;
    }

    private Decoder generateDecoder(Type type) {
        Decoder decoder = cfg.chooseDecoder.apply(this, type);
        if (decoder != null) {
            return decoder;
        }
        decoder = builtinDecoders.get(type);
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
        Map<TypeVariable, Type> typeArgs = new HashMap<>();
        Class clazz = TypeVariables.collect(type, typeArgs);
        if (clazz == null) {
            throw new QJsonDecodeException("can not cast to class: " + type);
        }
        boolean isJavaUtil = isJavaUtil(clazz);
        if (Collection.class.isAssignableFrom(clazz) && isJavaUtil) {
            return CollectionDecoder.create(this, clazz, typeArgs);
        }
        if (Map.class.isAssignableFrom(clazz) && isJavaUtil) {
            return MapDecoder.create(this, clazz, typeArgs);
        }
        return codegen.generateDecoder(clazz, typeArgs);
    }

    private static boolean isJavaUtil(Class clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.getName().startsWith("java.util.")) {
            return true;
        }
        return isJavaUtil(clazz.getSuperclass());
    }

    public String encode(Object val) {
        StringEncoderSink sink = new StringEncoderSink(new StringBuilder());
        sink.encodeObject(val, this);
        return sink.toString();
    }

    public String encode(Object val, StringBuilder builder) {
        builder.setLength(0);
        StringEncoderSink sink = new StringEncoderSink(builder);
        sink.encodeObject(val, this);
        return sink.toString();
    }

    public void encode(Object val, BytesBuilder bytesBuilder) {
        BytesEncoderSink sink = new BytesEncoderSink(bytesBuilder);
        sink.encodeObject(val, this);
    }

    public <T> T decode(Class<T> clazz, String encoded) {
        return (T) decode((Type) clazz, encoded);
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

    public <T> T decode(TypeLiteral<T> typeLiteral, String encoded) {
        return (T) decode(typeLiteral.$(), encoded);
    }

    private Object decode(Type type, byte[] encoded, int offset, int size) {
        Decoder decoder = decoderOf(type);
        BytesDecoderSource source = new BytesDecoderSource(encoded, offset, size);
        return source.decodeObject(decoder);
    }

    private Object decode(Type type, String encoded) {
        Decoder decoder = decoderOf(type);
        StringDecoderSource source = new StringDecoderSource(encoded);
        return source.decodeObject(decoder);
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

    public static boolean isSubType(Class baseClass, Type subType) {
        if (baseClass.equals(subType)) {
            return false;
        }
        if (subType instanceof Class) {
            return baseClass.isAssignableFrom((Class<?>) subType);
        }
        if (subType instanceof ParameterizedType) {
            return baseClass.isAssignableFrom((Class<?>) ((ParameterizedType) subType).getRawType());
        }
        return false;
    }
}
