package com.jsoniter.dson.decode;

import java.lang.reflect.Type;
import java.util.function.Function;

public class BytesIterator implements Iterator {

    private final Function<Type, Decoder> decoderProvider;
    final byte[] buf;
    int offset;
    final int size;

    public BytesIterator(Function<Type, Decoder> decoderProvider, byte[] buf, int offset, int size) {
        this.decoderProvider = decoderProvider;
        this.buf = buf;
        this.offset = offset;
        this.size = size;
    }

    public BytesIterator(byte[] buf) {
        this(type -> null, buf, 0, buf.length);
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

    void expect(char b1, char b2, char b3) {
        if (offset + 3 >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        boolean expected = buf[offset] == b1 && buf[offset + 1] == b2 && buf[offset + 2] == b3;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{b1, b2, b3}));
        }
        offset += 3;
    }

    void expect(char b1) {
        if (offset + 1 >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        boolean expected = buf[offset] == b1;
        if (!expected) {
            throw reportError("expect " + new String(new char[]{b1}));
        }
        offset += 1;
    }

    public DsonDecodeException reportError(String errMsg) {
        throw new DsonDecodeException(errMsg);
    }

    byte next() {
        if (offset >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return buf[offset++];
    }

    public byte[] borrowTemp(int capacity) {
        return new byte[capacity];
    }

    public void releaseTemp(byte[] temp) {
    }
}
