package com.jsoniter.dson.encode;

class EncodeLong {

    static void $(BytesStream stream, long val) {
        EncodeLong.$(stream, 'b', val);
    }

    static void $(BytesStream stream, char type, long val) {
        BytesBuilder builder = stream.bytesBuilder();
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', type);
        do {
            long masked = val & mask;
            builder.append((byte) (';' + masked));
            val >>>= 5;
        } while (val != 0);
        builder.append('"');
    }

    static void $(StringStream stream, long val) {
    }
}
