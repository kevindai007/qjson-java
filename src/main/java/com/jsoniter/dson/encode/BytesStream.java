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
        EncodeLong.$(this, val);
    }

    @Override
    public void encodeDouble(double val) {
        long l = Double.doubleToRawLongBits(val);
        EncodeLong.$(this, 'f', l);
    }

    @Override
    public void encodeString(String val) {
        EncodeString.$(this, val);
    }

    public void encodeBoolean(boolean val) {
        if (val) {
            builder.append('t', 'r', 'u', 'e');
        } else {
            builder.append('f', 'a', 'l', 's', 'e');
        }
    }

    @Override
    public void encodeNull() {
        builder.append('n', 'u', 'l', 'l');
    }

    @Override
    public DsonEncodeException reportError(String errMsg, Exception cause) {
        throw new DsonEncodeException(errMsg, cause);
    }

    public BytesBuilder bytesBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public byte[] borrowTemp(int capacity) {
        return new byte[capacity];
    }

    public void releaseTemp(byte[] temp) {
    }
}
