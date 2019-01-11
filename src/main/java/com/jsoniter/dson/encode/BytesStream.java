package com.jsoniter.dson.encode;

import java.lang.reflect.Type;
import java.util.function.Function;

public final class BytesStream implements Stream {

    private final Function<Type, Encoder> encoderProvider;
    private final BytesBuilder builder;

    public BytesStream(Function<Type, Encoder> encoderProvider, BytesBuilder builder) {
        this.encoderProvider = encoderProvider;
        this.builder = builder;
    }

    public BytesStream() {
        this(type -> null, new BytesBuilder());
    }

    @Override
    public void encodeLong(long val) {
        EncodeLong.$(builder, val);
    }

    @Override
    public void encodeDouble(double val) {
        long l = Double.doubleToRawLongBits(val);
        EncodeLong.$(builder, 'f', l);
    }

    public BytesBuilder bytesBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
