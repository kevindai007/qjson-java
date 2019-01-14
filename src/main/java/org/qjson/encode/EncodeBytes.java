package org.qjson.encode;

interface EncodeBytes {

    static void $(BytesEncoderSink sink, byte[] val) {
        BytesBuilder builder = sink.bytesBuilder();
        builder.append('"');
        int escapePos = shouldEscape(val);
        if (escapePos == -1) {
            builder.ensureCapacity(builder.length() + val.length + 1);
            System.arraycopy(val, 0, builder.getBuffer(), builder.length(), val.length);
            builder.setLength(builder.length() + val.length);
            builder.append('"');
            return;
        }
        int maxSize = 4 * val.length;
        builder.ensureCapacity(builder.length() + maxSize);
        System.arraycopy(val, 0, builder.getBuffer(), builder.length(), escapePos);
        builder.setLength(builder.length() + escapePos);
        escape(val, escapePos, builder);
        builder.append('"');
    }

    static void escape(byte[] buf, int escapePos, BytesBuilder builder) {
        for (int i = escapePos; i < buf.length;) {
            byte b = buf[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                builder.appendEscape(b);
                i++;
                continue;
            }
            // the byte[] might not be valid unicode
            if ((b & 0b10000000) == 0b00000000) {
                builder.append(b);
                i++;
                continue;
            } else if ((b & 0b11100000) == 0b11000000) {
                if (i + 1 >= buf.length) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                byte b2 = buf[i + 1];
                if (isInvalidByte(b2)) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                builder.append(b, b2);
                i += 2;
                continue;
            } else if ((b & 0b11110000) == 0b11100000) {
                // low surrogate: 0xED 0x9F 0xBF
                // high surrogate: 0xEE 0x80 0x80
                if (((byte) 0xED) <= b && b <= ((byte) 0xEF)) {
                    // to avoid half surrogate
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                if (i + 2 >= buf.length) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                byte b2 = buf[i + 1];
                byte b3 = buf[i + 2];
                if (isInvalidByte(b2) || isInvalidByte(b3)) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                builder.append(b, b2, b3);
                i += 3;
                continue;
            } else if ((b & 0b11111000) == 0b11110000) {
                if (i + 3 >= buf.length) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                byte b2 = buf[i + 1];
                byte b3 = buf[i + 2];
                byte b4 = buf[i + 3];
                if (isInvalidByte(b2) || isInvalidByte(b3) || isInvalidByte(b4)) {
                    builder.appendEscape(b);
                    i++;
                    continue;
                }
                builder.append(b, b2, b3, b4);
                i += 4;
                continue;
            }
            builder.appendEscape(b);
            i++;
        }
    }

    static int shouldEscape(byte[] buf) {
        for (int i = 0; i < buf.length; ) {
            byte b = buf[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                return i;
            }
            // the byte[] might not be valid unicode
            if ((b & 0b10000000) == 0b00000000) {
                i++;
                continue;
            } else if ((b & 0b11100000) == 0b11000000) {
                if (i + 1 >= buf.length) {
                    return i;
                }
                if (isInvalidByte(buf[i + 1])) {
                    return i;
                }
                i += 2;
                continue;
            } else if ((b & 0b11110000) == 0b11100000) {
                // low surrogate: 0xED 0x9F 0xBF
                // high surrogate: 0xEE 0x80 0x80
                if (((byte) 0xED) <= b && b <= ((byte) 0xEF)) {
                    // to avoid half surrogate
                    return i;
                }
                if (i + 2 >= buf.length) {
                    return i;
                }
                if (isInvalidByte(buf[i + 1]) || isInvalidByte(buf[i + 2])) {
                    return i;
                }
                i += 3;
                continue;
            } else if ((b & 0b11111000) == 0b11110000) {
                if (i + 3 >= buf.length) {
                    return i;
                }
                if (isInvalidByte(buf[i + 1]) || isInvalidByte(buf[i + 2]) || isInvalidByte(buf[i + 3])) {
                    return i;
                }
                i += 4;
                continue;
            }
            return i;
        }
        return -1;
    }

    static boolean isInvalidByte(byte b) {
        return (b & 0b11000000) != 0b10000000;
    }
}
