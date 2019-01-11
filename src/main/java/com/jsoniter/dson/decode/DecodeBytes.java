package com.jsoniter.dson.decode;

import com.jsoniter.dson.encode.BytesBuilder;

interface DecodeBytes {

    static byte[] $(BytesIterator iter) {
        iter.expect('"');
        int i = iter.offset;
        for (; i < iter.size; i++) {
            byte b = iter.buf[i];
            if (b == '"') {
                byte[] decoded = new byte[i - iter.offset];
                System.arraycopy(iter.buf, iter.offset, decoded, 0, decoded.length);
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
            if (DecodeString.readEscaped(iter, builder)) {
                break;
            }
            if (DecodeString.readRaw(iter, builder)) {
                break;
            }
        }
        byte[] decoded = new byte[builder.getLength()];
        System.arraycopy(builder.getBuffer(), 0, decoded, 0, decoded.length);
        iter.releaseTemp(builder.getBuffer());
        return decoded;
    }
}
