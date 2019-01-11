package com.jsoniter.dson.marshal;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BytesBuilder {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private byte[] buf;
    private int len;

    public BytesBuilder(int capacity) {
        buf = new byte[capacity];
    }

    public BytesBuilder() {
        this(16);
    }

    public void append(byte b) {
        ensureCapacity(len + 1);
        buf[len++] = b;
    }

    public byte[] getBuffer() {
        return buf;
    }

    public void setLength(int newLen) {
        len = newLen;
    }

    public int getLength() {
        return len;
    }

    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity - buf.length > 0) {
            buf = Arrays.copyOf(buf,
                    newCapacity(minimumCapacity));
        }
    }

    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int newCapacity = (buf.length << 1) + 2;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
                ? hugeCapacity(minCapacity)
                : newCapacity;
    }

    private int hugeCapacity(int minCapacity) {
        if (Integer.MAX_VALUE - minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE)
                ? minCapacity : MAX_ARRAY_SIZE;
    }

    @Override
    public String toString() {
        return new String(buf, 0, len, StandardCharsets.UTF_8);
    }
}
