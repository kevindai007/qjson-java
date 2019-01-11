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

    public DsonUnmarshalException reportError(String errMsg) {
        throw new DsonUnmarshalException(errMsg);
    }

    byte next() {
        if (offset >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return buf[offset++];
    }
}
