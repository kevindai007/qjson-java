package org.qjson.encode;

import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

public class StringEncoderSink implements EncoderSink {

    private final PathTracker pathTracker = new PathTracker(this);
    private final StringBuilder builder;

    public StringEncoderSink() {
        this(new StringBuilder());
    }

    public StringEncoderSink(StringBuilder builder) {
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
    public void encodeObject(Object val, Encoder encoder) {
        pathTracker.encodeObject(val, encoder);
    }

    @Override
    public void encodeObject(Object val, Encoder.Provider spi) {
        pathTracker.encodeObject(val, spi);
    }

    @Override
    public CurrentPath currentPath() {
        return pathTracker.currentPath();
    }

    @Override
    public void encodeRef(String ref) {
        builder.append("\"\\/");
        EncodeString.body(this, ref);
        builder.append('"');
    }

    @Override
    public void write(String raw) {
        builder.append(raw);
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

    @Override
    public <T> T borrowTemp(Class<T> clazz) {
        return null;
    }

    @Override
    public void releaseTemp(Object temp) {

    }

    public StringBuilder stringBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public void reset() {
        pathTracker.reset();
        builder.setLength(0);
    }
}
