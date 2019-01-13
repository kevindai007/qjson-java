package com.jsoniter.dson.encode;

class EncodeLong {

    static void $(BytesEncoderSink sink, int val) {
        BytesBuilder builder = sink.bytesBuilder();
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', 'b');
        builder.append(
                (byte) (';' + ((val >>> 30) & mask)),
                (byte) (';' + ((val >>> 25) & mask)),
                (byte) (';' + ((val >>> 20) & mask)),
                (byte) (';' + ((val >>> 15) & mask)),
                (byte) (';' + ((val >>> 10) & mask)),
                (byte) (';' + ((val >>> 5) & mask)),
                (byte) (';' + (val & mask))
        );
        builder.append('"');
    }

    static void $(BytesEncoderSink sink, long val) {
        EncodeLong.$(sink, 'b', val);
    }

    static void $(BytesEncoderSink sink, char type, long val) {
        BytesBuilder builder = sink.bytesBuilder();
        int mask = (1 << 5) - 1;
        builder.append('"', '\\', type);
        builder.append(
                (byte) (';' + ((val >>> 63) & mask)),
                (byte) (';' + ((val >>> 60) & mask)),
                (byte) (';' + ((val >>> 55) & mask)),
                (byte) (';' + ((val >>> 50) & mask)),
                (byte) (';' + ((val >>> 45) & mask)),
                (byte) (';' + ((val >>> 40) & mask)),
                (byte) (';' + ((val >>> 35) & mask)),
                (byte) (';' + ((val >>> 30) & mask)),
                (byte) (';' + ((val >>> 25) & mask)),
                (byte) (';' + ((val >>> 20) & mask)),
                (byte) (';' + ((val >>> 15) & mask)),
                (byte) (';' + ((val >>> 10) & mask)),
                (byte) (';' + ((val >>> 5) & mask)),
                (byte) (';' + (val & mask))
        );
        builder.append('"');
    }

    static void $(StringEncoderSink sink, int val) {
        StringBuilder builder = sink.stringBuilder();
        int mask = (1 << 5) - 1;
        Append.$(builder,
                '"', '\\', 'b',
                (char) (';' + ((val >>> 30) & mask)),
                (char) (';' + ((val >>> 25) & mask)),
                (char) (';' + ((val >>> 20) & mask)),
                (char) (';' + ((val >>> 15) & mask)),
                (char) (';' + ((val >>> 10) & mask)),
                (char) (';' + ((val >>> 5) & mask)),
                (char) (';' + (val & mask)),
                '"'
        );
    }
}
