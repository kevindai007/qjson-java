package org.qjson.encode;

import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.util.function.Function;

public class StringEncoderSink implements EncoderSink {

    private final Function<Class, Encoder> encoderProvider;
    private final StringBuilder builder;

    public StringEncoderSink() {
        this(type -> {
            throw new QJsonEncodeException("can not encode: " + type);
        }, new StringBuilder());
    }

    public StringEncoderSink(Function<Class, Encoder> encoderProvider, StringBuilder builder) {
        this.encoderProvider = encoderProvider;
        this.builder = builder;
    }

    @Override
    public void encodeNull() {
        Append.$(builder, 'n', 'u', 'l', 'l');
    }

    @Override
    public void encodeBoolean(boolean val) {
        if (val) {
            Append.$(builder, 't', 'r', 'u', 'e');
        } else {
            Append.$(builder, 'f', 'a', 'l', 's', 'e');
        }
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
        Encoder encoder = encoderProvider.apply(val.getClass());
        encoder.encode(this, val);
    }

    @Override
    public void write(char b) {
        builder.append(b);
    }

    @Override
    public QJsonEncodeException reportError(String errMsg) {
        throw new QJsonEncodeException(errMsg);
    }

    @Override
    public QJsonEncodeException reportError(String errMsg, Exception cause) {
        throw new QJsonEncodeException(errMsg, cause);
    }

    public StringBuilder stringBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
