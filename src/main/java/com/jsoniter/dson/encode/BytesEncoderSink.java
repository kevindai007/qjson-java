package com.jsoniter.dson.encode;

import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;

import java.util.function.Function;

public final class BytesEncoderSink implements EncoderSink {

    private final Function<Class, Encoder> encoderProvider;
    private final BytesBuilder builder;

    public BytesEncoderSink(Function<Class, Encoder> encoderProvider, BytesBuilder builder) {
        this.encoderProvider = encoderProvider;
        this.builder = builder;
    }

    public BytesEncoderSink() {
        this(type -> null, new BytesBuilder());
    }

    @Override
    public void encodeInt(int val) {
        EncodeLong.$(this, val);
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

    @Override
    public void encodeBytes(byte[] val) {
        EncodeBytes.$(this, val);
    }

    @Override
    public void encodeObject(Object val) {
        if (val == null) {
            encodeNull();
            return;
        }
        encoderProvider.apply(val.getClass()).encode(this, val);
    }

    @Override
    public void write(char b) {
        builder.append(b);
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
    public DsonEncodeException reportError(String errMsg) {
        throw new DsonEncodeException(errMsg);
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
