package com.jsoniter.dson.decode;

interface DecodeLong {

    static long $(BytesDecoderSource iter) {
        return DecodeLong.$(iter, 'b');
    }

    static long $(BytesDecoderSource iter, char type) {
        iter.expect('"', '\\', type);
        long val = 0;
        int i = iter.offset;
        for (; i < iter.size; i++) {
            if (iter.buf[i] == '"') {
                iter.offset = i + 1;
                return val;
            }
            val = (val << 5) + (iter.buf[i] - ';');
        }
        throw iter.reportError("missing double quote");
    }
}
