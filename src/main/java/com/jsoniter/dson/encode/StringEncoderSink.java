package com.jsoniter.dson.encode;

import com.jsoniter.dson.spi.EncoderSink;

public class StringEncoderSink implements EncoderSink {

    private final StringBuilder builder;

    public StringEncoderSink() {
        this(new StringBuilder());
    }

    public StringEncoderSink(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void encodeNull() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeBoolean(boolean val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeInt(int val) {
        EncodeLong.$(this, val);
    }

    @Override
    public void encodeLong(long val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeDouble(double val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeString(String val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeBytes(byte[] val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void encodeObject(Object val) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void write(char b) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public DsonEncodeException reportError(String errMsg) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public DsonEncodeException reportError(String errMsg, Exception cause) {
        throw new UnsupportedOperationException("not implemented");
    }

    public StringBuilder stringBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
