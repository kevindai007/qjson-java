package org.qjson.decode;

import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;

import java.lang.reflect.Type;

public class StringDecoderSource implements DecoderSource {

    private final Decoder.Provider spi;
    final String buf;
    int offset;

    public StringDecoderSource(Decoder.Provider spi, String buf) {
        this.spi = spi;
        this.buf = buf;
    }

    @Override
    public boolean decodeNull() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean decodeBoolean() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int decodeInt() {
        return (int) DecodeLong.$(this);
    }

    @Override
    public long decodeLong() {
        return DecodeLong.$(this);
    }

    @Override
    public double decodeDouble() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String decodeString() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object decodeStringOrNumber() {
        if (offset + 2 < buf.length() && buf.charAt(offset + 1) == '\\') {
            char type = buf.charAt(offset + 2);
            if (type == 'b') {
                return decodeLong();
            } else if (type == 'f') {
                return decodeDouble();
            } else {
                throw reportError("expect \\b or \\f");
            }
        }
        return decodeString();
    }

    @Override
    public byte[] decodeBytes() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object decodeObject(Type type) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public byte read() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public byte peek() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void next() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void skip() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public QJsonDecodeException reportError(String errMsg) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public QJsonDecodeException reportError(String errMsg, Exception cause) {
        throw new UnsupportedOperationException("not implemented");
    }

    void expect(char c1, char c2, char c3) {
        if (offset + 3 > buf.length()) {
            throw reportError("expect 3 more bytes");
        }
        boolean expected = buf.charAt(offset) == c1 && buf.charAt(offset + 1) == c2 && buf.charAt(offset + 2) == c3;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{c1, c2, c3}));
        }
        offset += 3;
    }
}
