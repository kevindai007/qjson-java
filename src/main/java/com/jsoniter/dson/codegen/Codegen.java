package com.jsoniter.dson.codegen;

import com.jsoniter.dson.decode.Decoder;
import com.jsoniter.dson.encode.Encoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Codegen {

    private final Map<Class, Encoder> generatedEncoders = new ConcurrentHashMap<>();

    public Decoder generateDecoder(Class clazz) {
        throw new UnsupportedOperationException();
    }

    public Encoder generateEncoder(Class clazz) {
        Encoder encoder = generatedEncoders.get(clazz);
        if (encoder != null) {
            return encoder;
        }
        throw new UnsupportedOperationException();
    }
}
