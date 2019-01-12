package com.jsoniter.dson;

import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesEncoderSink;
import com.jsoniter.dson.spi.Encoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSON {

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
        if (String.class.equals(clazz)) {
            return (sink, val) -> sink.encodeString((String) val);
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
