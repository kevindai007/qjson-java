package org.qjson.decode;

import org.qjson.encode.CurrentPath;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;

public class StringDecoderSource implements DecoderSource {

    private final PathTracker pathTracker = new PathTracker(this);
    private final Object[] attachments = new Object[DecoderSource.AttachmentKey.count()];
    final String buf;
    int offset;

    public StringDecoderSource(String buf) {
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
        expect('"');
        return DecodeString.$(this, offset);
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
    public Object decodeRef(Decoder decoder) {
        if (offset + 2 < buf.length() && buf.charAt(offset) == '"' && buf.charAt(offset + 1) == '\\' && buf.charAt(offset + 2) == '/') {
            String path = DecodeString.$(this, offset + 3);
            Object ref = pathTracker.lookup(path);
            return decoder.decodeRef(this, path, ref);
        }
        return null;
    }

    @Override
    public Object decodeObject(Decoder decoder) {
        return pathTracker.decodeObject(decoder);
    }

    @Override
    public CurrentPath currentPath() {
        return pathTracker.currentPath();
    }

    @Override
    public <T> T getAttachment(AttachmentKey<T> key) {
        return (T) attachments[key.val];
    }

    @Override
    public <T> void setAttachment(AttachmentKey<T> key, T attachment) {
        attachments[key.val] = attachment;
    }

    @Override
    public int mark() {
        return offset;
    }

    @Override
    public String sinceMark(int mark) {
        return buf.substring(mark, offset);
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
}
