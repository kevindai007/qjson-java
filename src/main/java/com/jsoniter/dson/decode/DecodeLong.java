package com.jsoniter.dson.decode;

interface DecodeLong {

    static long $(BytesIterator iter) {
        return DecodeLong.$(iter, 'b');
    }

    static long $(BytesIterator iter, char type) {
        iter.expect('"', '\\', type);
        long val = 0;
        int i = iter.offset;
        for (; i < iter.size; i++) {
            if (iter.buf[i] == '"') {
                break;
            }
        }
        int end = i;
        if (end >= iter.size) {
            throw iter.reportError("missing double quote");
        }
        for (i = end - 1; i >= iter.offset; i--) {
            val = (val << 5) + (iter.buf[i] - ';');
        }
        iter.offset = end;
        return val;
    }
}
