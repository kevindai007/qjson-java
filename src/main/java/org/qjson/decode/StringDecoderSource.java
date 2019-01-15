package org.qjson.decode;

import org.qjson.encode.BytesBuilder;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;

import java.lang.reflect.Type;

public class StringDecoderSource implements DecoderSource {

    private final Decoder.Provider spi;
    final String buf;
    int offset;
    private BytesBuilder temp;

    public StringDecoderSource(Decoder.Provider spi, String buf) {
        this.spi = spi;
        this.buf = buf;
    }

    @Override
    public boolean decodeNull() {
        if (offset + 4 > buf.length()) {
            return false;
        }
        boolean isNull = buf.charAt(offset) == 'n' && buf.charAt(offset + 1) == 'u' && buf.charAt(offset + 2) == 'l' && buf.charAt(offset + 3) == 'l';
        if (isNull) {
            offset += 4;
        }
        return isNull;
    }

    @Override
    public boolean decodeBoolean() {
        if (offset + 4 > buf.length()) {
            throw reportError("expect true or false");
        }
        boolean isTrue = buf.charAt(offset) == 't' && buf.charAt(offset + 1) == 'r' && buf.charAt(offset + 2) == 'u' && buf.charAt(offset + 3) == 'e';
        if (isTrue) {
            offset += 4;
            return true;
        }
        expect('f', 'a', 'l', 's', 'e');
        return false;
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
        long l = DecodeLong.$(this, 'f');
        return Double.longBitsToDouble(l);
    }

    @Override
    public String decodeString() {
        return DecodeString.$(this);
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
        return DecodeBytes.$(this);
    }

    @Override
    public Object decodeObject(Type type) {
        if (decodeNull()) {
            return null;
        }
        return spi.decoderOf(type).decode(this);
    }

    @Override
    public byte read() {
        char c = buf.charAt(offset);
        if (c < 128) {
            offset++;
            return (byte) c;
        }
        throw reportError("expect ascii");
    }

    @Override
    public byte peek() {
        char c = buf.charAt(offset);
        if (c < 128) {
            return (byte) c;
        }
        throw reportError("expect ascii");
    }

    @Override
    public void next() {
        offset++;
    }

    @Override
    public void skip() {
        Skip.$(this);
    }


    @Override
    public QJsonDecodeException reportError(String errMsg) {
        throw new QJsonDecodeException(errMsg);
    }

    @Override
    public QJsonDecodeException reportError(String errMsg, Exception cause) {
        throw new QJsonDecodeException(errMsg, cause);
    }

    void expect(char c1, char c2, char c3, char c4, char c5) {
        if (offset + 5 > buf.length()) {
            throw reportError("expect 5 more bytes");
        }
        boolean expected = buf.charAt(offset) == c1
                && buf.charAt(offset + 1) == c2
                && buf.charAt(offset + 2) == c3
                && buf.charAt(offset + 3) == c4
                && buf.charAt(offset + 4) == c5;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{c1, c2, c3, c4, c5}));
        }
        offset += 5;
    }

    void expect(char c1, char c2, char c3, char c4) {
        if (offset + 4 > buf.length()) {
            throw reportError("expect 4 more bytes");
        }
        boolean expected = buf.charAt(offset) == c1
                && buf.charAt(offset + 1) == c2
                && buf.charAt(offset + 2) == c3
                && buf.charAt(offset + 3) == c4;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{c1, c2, c3, c4}));
        }
        offset += 4;
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

    void expect(char c) {
        if (offset >= buf.length()) {
            throw reportError("expect more bytes");
        }
        boolean expected = buf.charAt(offset) == c;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{c}));
        }
        offset++;
    }

    public BytesBuilder borrowTemp(int capacity) {
        if (temp == null) {
            return new BytesBuilder(new byte[capacity], 0);
        }
        temp.ensureCapacity(capacity);
        return temp;
    }

    public void releaseTemp(BytesBuilder temp) {
        temp.setLength(0);
        this.temp = temp;
    }
}
