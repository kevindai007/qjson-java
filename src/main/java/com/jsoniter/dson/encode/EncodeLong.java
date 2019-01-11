package com.jsoniter.dson.encode;

class EncodeLong {

    static void $(BytesBuilder builder, long val) {
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', 'b');
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
