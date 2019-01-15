package org.qjson.encode;

import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public final class BytesEncoderSink implements EncoderSink {

    private final PathTracker pathTracker = new PathTracker(this);
    private final TempHolder tempHolder = new TempHolder();
    private final BytesBuilder builder;

    public BytesEncoderSink(BytesBuilder builder) {
        this.builder = builder;
    }

    public BytesEncoderSink() {
        this(new BytesBuilder());
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
        return null;
    }

    @Override
    public void encodeRef(String ref) {
        builder.append('"', '\\', '\\');
        EncodeString.body(this, ref);
        builder.append('"');
    }

    @Override
    public void write(String raw) {
        builder.ensureCapacity(builder.length()  + raw.length() * 3);
        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.buf(), builder.length(),
                builder.buf().length - builder.length());
        CharBuffer charBuffer = CharBuffer.wrap(raw);
        CoderResult result = StandardCharsets.UTF_8.newEncoder().encode(charBuffer, byteBuffer, true);
        if (result.isError()) {
            try {
                result.throwException();
            } catch (Exception e) {
                throw reportError("encode string to utf8 failed", e);
            }
        }
        builder.setLength(byteBuffer.position());
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

    @Override
    public <T> T borrowTemp(Class<T> clazz) {
        return tempHolder.borrowTemp(clazz);
    }

    @Override
    public void releaseTemp(Object temp) {
        tempHolder.releaseTemp(temp);
    }

    public BytesBuilder bytesBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public byte[] copyOfBytes() {
        return builder.copyOfBytes();
    }
}
