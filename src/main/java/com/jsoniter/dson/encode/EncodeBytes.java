package com.jsoniter.dson.encode;

interface EncodeBytes {

    static void $(BytesStream stream, byte[] val) {
        BytesBuilder builder = stream.bytesBuilder();
        builder.append('"');
        int escapePos = shouldEscape(val);
        if (escapePos == -1) {
            builder.ensureCapacity(val.length + 1);
            System.arraycopy(val, 0, builder.getBuffer(), builder.getLength(), val.length);
            builder.setLength(builder.getLength() + val.length);
            builder.append('"');
            return;
        }
        int maxSize = 4 * val.length;
        builder.ensureCapacity(maxSize);
        System.arraycopy(val, 0, builder.getBuffer(), builder.getLength(), escapePos);
        builder.setLength(builder.getLength() + escapePos);
        for (int i = escapePos; i < val.length; i++) {
            escape(val, i, builder);
        }
        builder.append('"');
    }

    static void escape(byte[] val, int i, BytesBuilder builder) {
        byte b = val[i];
        boolean isControlCharacter = 0 <= b && b < 0x20;
        if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
            builder.append('\\', '/', (char) ('A' + (b >>> 4)), (char)('A' + (b & 0xF)));
        } else {
            builder.append(b);
        }
    }

    static int shouldEscape(byte[] buf) {
        for (int i = 0; i < buf.length; i++) {
            byte b = buf[i];
            boolean isPrintable = 0x20 <= b && b <= 0x7e;
            if (!isPrintable || b == '\\' || b == '/' || b == '"') {
                return i;
            }
        }
        return -1;
    }
}
