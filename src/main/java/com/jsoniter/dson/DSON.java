package com.jsoniter.dson;

import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesEncoderSink;
import com.jsoniter.dson.spi.Encoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSON {

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
    }};
    private final Map<Class, Encoder> encoderCache = new ConcurrentHashMap<>();
    private final Codegen codegen;

    public DSON(Codegen codegen) {
        this.codegen = codegen;
    }

    public DSON() {
        this(new Codegen());
    }

    public Encoder encoderOf(Class clazz) {
        return encoderCache.computeIfAbsent(clazz, this::generateEncoder);
    }

    private Encoder generateEncoder(Class clazz) {
        Encoder encoder = builtinEncoders.get(clazz);
        if (encoder != null) {
            return encoder;
        }
        return codegen.generateEncoder(clazz);
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
}
