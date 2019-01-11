package com.jsoniter.dson.decode;

import com.jsoniter.dson.encode.BytesBuilder;

import java.nio.charset.StandardCharsets;

interface DecodeString {

    static String $(BytesIterator iter) {
        iter.expect('"');
        int i = iter.offset;
        for (; i < iter.size; i++) {
            byte b = iter.buf[i];
            if (b == '"') {
                String decoded = new String(iter.buf, iter.offset, i - iter.offset, StandardCharsets.UTF_8);
                iter.offset = i + 1;
                return decoded;
            }
            if (b == '\\') {
                break;
            }
        }
        if (i >= iter.size) {
            throw iter.reportError("missing double quote");
        }
        int noEscapeLen = i - iter.offset;
        byte[] temp = iter.borrowTemp(noEscapeLen + 16);
        System.arraycopy(iter.buf, iter.offset, temp, 0, noEscapeLen);
        BytesBuilder builder = new BytesBuilder(temp, noEscapeLen);
        iter.offset = i;
        while (true) {
            if (readEscaped(iter, builder)) {
                break;
            }
            if (readRaw(iter, builder)) {
                break;
            }
        }
        String decoded = builder.toString();
        iter.releaseTemp(builder.getBuffer());
        return decoded;
    }

    static boolean readEscaped(BytesIterator iter, BytesBuilder builder) {
        for (int i = iter.offset; i < iter.size; ) {
            byte b = iter.buf[i];
            if (b == '"') {
                iter.offset = i + 1;
                return true;
            }
            if (b != '\\') {
                iter.offset = i;
                return false;
            }
            if (i + 4 >= iter.size) {
                throw iter.reportError("missing double quote");
            }
            if (iter.buf[i + 1] != '/') {
                throw iter.reportError("escape \\/ is the only supported escape form");
            }
            b = (byte) (((iter.buf[i + 2] - 'A') << 4) + iter.buf[i + 3] - 'A');
            builder.append(b);
            i += 4;
        }
        throw iter.reportError("missing double quote");
    }

    static boolean readRaw(BytesIterator iter, BytesBuilder builder) {
        for (int i = iter.offset; i < iter.size; i++) {
            byte b = iter.buf[i];
            if (b == '"') {
                iter.offset = i + 1;
                return true;
            }
            if (b == '\\') {
                iter.offset = i;
                return false;
            }
            builder.append(b);
        }
        throw iter.reportError("missing double quote");
    }
}
