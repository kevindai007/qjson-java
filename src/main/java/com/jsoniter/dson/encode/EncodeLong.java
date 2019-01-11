package com.jsoniter.dson.encode;

class EncodeLong {

    static void $(BytesStream stream, long val) {
        EncodeLong.$(stream, 'b', val);
    }

    static void $(BytesStream stream, char type, long val) {
        BytesBuilder builder = stream.bytesBuilder();
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', type);
        builder.append(
                (byte)(';' + ((val >>> 63) & mask)),
                (byte)(';' + ((val >>> 60) & mask)),
                (byte)(';' + ((val >>> 55) & mask)),
                (byte)(';' + ((val >>> 50) & mask)),
                (byte)(';' + ((val >>> 45) & mask)),
                (byte)(';' + ((val >>> 40) & mask)),
                (byte)(';' + ((val >>> 35) & mask)),
                (byte)(';' + ((val >>> 30) & mask)),
                (byte)(';' + ((val >>> 25) & mask)),
                (byte)(';' + ((val >>> 20) & mask)),
                (byte)(';' + ((val >>> 15) & mask)),
                (byte)(';' + ((val >>> 10) & mask)),
                (byte)(';' + ((val >>> 5) & mask)),
                (byte) (';' + (val & mask))
        );
        do {
            val >>>= 5;
        } while (val != 0);
        builder.append('"');
    }

    static void $(StringStream stream, long val) {
    }
}
