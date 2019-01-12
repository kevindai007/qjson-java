package com.jsoniter.dson;

import com.jsoniter.dson.any.AnyList;
import com.jsoniter.dson.any.AnyMap;
import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.decode.BytesDecoderSource;
import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesEncoderSink;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.Encoder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DSON {

    public static class Config {
        public Codegen codegen;
        public Function<Class, Class> chooseImpl;
        public BiFunction<DSON, Class, Decoder> decoderProvider;
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
        put(Map.class, new MapEncoder());
        put(Iterable.class, new IterableEncoder());
    }};
    private final Map<Class, Decoder> builtinDecoders = new HashMap<Class, Decoder>() {{

    }};
    private final Map<Class, Encoder> encoderCache = new ConcurrentHashMap<>();
    private final Map<Class, Decoder> decoderCache = new ConcurrentHashMap<>();
    private final Config config;

    public DSON(Config config) {
        if (config.codegen == null) {
            config.codegen = new Codegen();
        }
        Map<Class, Class> implMap = new HashMap<Class, Class>() {{
            put(Map.class, AnyMap.class);
            put(Iterable.class, AnyList.class);
            put(Collection.class, AnyList.class);
            put(List.class, AnyList.class);
            put(Set.class, HashSet.class);
        }};
        if (config.chooseImpl == null) {
            config.chooseImpl = implMap::get;
        } else {
            Function<Class, Class> userChooseImpl = config.chooseImpl;
            config.chooseImpl = clazz -> {
                Class impl = userChooseImpl.apply(clazz);
                if (impl != null) {
                    return impl;
                }
                return implMap.get(clazz);
            };
        }
        this.config = config;
        builtinDecoders.put(Object.class, new ObjectDecoder(
                decoderOf(config.chooseImpl.apply(List.class)),
                decoderOf(config.chooseImpl.apply(Map.class))));
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
        if (Map.class.isAssignableFrom(clazz)) {
            return builtinEncoders.get(Map.class);
        }
        if (Iterable.class.isAssignableFrom(clazz)) {
            return builtinEncoders.get(Iterable.class);
        }
        return config.codegen.generateEncoder(clazz);
    }

    public Decoder decoderOf(Class clazz) {
        return decoderCache.computeIfAbsent(clazz, this::generateDecoder);
    }

    private Decoder generateDecoder(Class clazz) {
        Decoder decoder = builtinDecoders.get(clazz);
        if (decoder != null) {
            return decoder;
        }
        return config.codegen.generateDecoder(clazz);
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
        BytesDecoderSource source = new BytesDecoderSource(this::decoderOf, encoded, offset, size);
        return source.decodeObject(clazz);
    }
}
