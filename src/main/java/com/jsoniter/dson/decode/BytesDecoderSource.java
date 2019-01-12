package com.jsoniter.dson.decode;

import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class BytesDecoderSource implements DecoderSource {

    private final Function<Type, Decoder> decoderProvider;
    final byte[] buf;
    int offset;
    final int size;

    public BytesDecoderSource(Function<Type, Decoder> decoderProvider, byte[] buf, int offset, int size) {
        this.decoderProvider = decoderProvider;
        this.buf = buf;
        this.offset = offset;
        this.size = size;
    }

    public BytesDecoderSource(String buf) {
        this(buf.getBytes(StandardCharsets.UTF_8));
    }

    public BytesDecoderSource(byte[] buf) {
        this(type -> null, buf, 0, buf.length);
    }

    @Override
    public int decodeInt() {
        return (int) decodeLong();
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
        if (offset + 2 < size && buf[offset + 1] == '\\') {
            byte type = buf[offset + 2];
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
        return decoderProvider.apply(type).decode(this);
    }

    @Override
    public byte read() {
        byte b = peek();
        next();
        return b;
    }

    public boolean decodeBoolean() {
        if (offset + 4 > size) {
            throw reportError("expect true or false");
        }
        boolean isTrue = buf[offset] == 't' && buf[offset + 1] == 'r' && buf[offset + 2] == 'u' && buf[offset + 3] == 'e';
        if (isTrue) {
            offset += 4;
            return true;
        }
        expect('f', 'a', 'l', 's', 'e');
        return false;
    }

    @Override
    public boolean decodeNull() {
        if (offset + 4 > size) {
            return false;
        }
        boolean isNull = buf[offset] == 'n' && buf[offset + 1] == 'u' && buf[offset + 2] == 'l' && buf[offset + 3] == 'l';
        if (isNull) {
            offset += 4;
        }
        return isNull;
    }

    void expect(char b1, char b2, char b3, char b4, char b5) {
        if (offset + 5 > size) {
            throw reportError("expect 5 more bytes");
        }
        boolean expected = buf[offset] == b1
                && buf[offset + 1] == b2
                && buf[offset + 2] == b3
                && buf[offset + 3] == b4
                && buf[offset + 4] == b5;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{b1, b2, b3, b4, b5}));
        }
        offset += 5;
    }

    void expect(char b1, char b2, char b3) {
        if (offset + 3 > size) {
            throw reportError("expect 3 more bytes");
        }
        boolean expected = buf[offset] == b1 && buf[offset + 1] == b2 && buf[offset + 2] == b3;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{b1, b2, b3}));
        }
        offset += 3;
    }

    void expect(char b1) {
        if (offset >= size) {
            throw reportError("expect more bytes");
        }
        boolean expected = buf[offset] == b1;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{b1}));
        }
        offset ++;
    }

    public DsonDecodeException reportError(String errMsg) {
        throw new DsonDecodeException(errMsg);
    }

    public DsonDecodeException reportError(String errMsg, Exception cause) {
        throw new DsonDecodeException(errMsg, cause);
    }

    public byte peek() {
        if (offset >= size) {
            throw reportError("expect more byte");
        }
        return buf[offset];
    }

    public void next() {
        offset++;
    }

    public byte[] borrowTemp(int capacity) {
        return new byte[capacity];
    }

    public void releaseTemp(byte[] temp) {
    }
}
