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
                return val;
            }
            val = (val << 5) + (iter.buf[i] - ';');
        }
        throw iter.reportError("missing double quote");
    }
}
