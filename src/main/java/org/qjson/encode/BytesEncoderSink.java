package org.qjson.encode;

import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

public final class BytesEncoderSink implements EncoderSink {

    private final Encoder.Provider spi;
    private final BytesBuilder builder;
    private byte[] temp;

    public BytesEncoderSink(Encoder.Provider spi, BytesBuilder builder) {
        this.spi = spi;
        this.builder = builder;
    }

    public BytesEncoderSink(Encoder.Provider spi) {
        this(spi, new BytesBuilder());
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
        Encoder encoder = spi.encoderOf(val.getClass());
        encoder.encode(this, val);
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
    public QJsonEncodeException reportError(String errMsg) {
        throw new QJsonEncodeException(errMsg);
    }

    @Override
    public QJsonEncodeException reportError(String errMsg, Exception cause) {
        throw new QJsonEncodeException(errMsg, cause);
    }

    public BytesBuilder bytesBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public byte[] borrowTemp(int capacity) {
        if (temp == null || temp.length < capacity) {
            return new byte[capacity];
        }
        byte[] borrowed = this.temp;
        this.temp = null;
        return borrowed;
    }

    public void releaseTemp(byte[] temp) {
        this.temp = temp;
    }

    public byte[] copyOfBytes() {
        return builder.copyOfBytes();
    }
}
