package com.jsoniter.dson.marshal;

import java.lang.reflect.Type;
import java.util.function.Function;

public final class BytesStream implements Stream {

    private final Function<Type, Encoder> encoderProvider;
    private BytesBuilder builder;

    public BytesStream(Function<Type, Encoder> encoderProvider, BytesBuilder builder) {
        this.encoderProvider = encoderProvider;
    }

    public BytesStream() {
        this(type -> null, new BytesBuilder());
    }

    @Override
    public void encodeInt(int val) {
        int reminder = val % 0xFF;
    }

    public BytesBuilder bytesBuilder() {
        return builder;
    }
}
