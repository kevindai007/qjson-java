package com.jsoniter.dson.encode;

class EncodeLong {

    static void $(BytesBuilder builder, long val) {
        EncodeLong.$(builder, 'b', val);
    }

    static void $(BytesBuilder builder, char type, long val) {
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', type);
        do {
            long masked = val & mask;
            builder.append((byte) (';' + masked));
            val >>>= 5;
        } while (val != 0);
        builder.append('"');
    }

    static void $(StringBuilder builder, long val) {
    }
}
