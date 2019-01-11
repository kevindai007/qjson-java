package com.jsoniter.dson.encode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class EncodeString {

    static void $(BytesStream stream, String val) {
        BytesBuilder builder = stream.bytesBuilder();
        builder.append('"');
        int maxSize = 3 * val.length();
        builder.ensureCapacity(maxSize);
        int offset = builder.getLength();
        byte[] buf = builder.getBuffer();
        ByteBuffer byteBuf = ByteBuffer.wrap(buf, offset, maxSize);
        CoderResult result = StandardCharsets.UTF_8.newEncoder().encode(
                CharBuffer.wrap(val),
                byteBuf, true);
        if (result.isError()) {
            try {
                result.throwException();
            } catch (Exception e) {
                throw stream.reportError("encode string to utf8 failed", e);
            }
        }
        int end = byteBuf.position();
        int escapePos = shouldEscape(buf, offset, end);
        if (escapePos == -1) {
            builder.setLength(end);
            builder.append('"');
            return;
        }
        builder.setLength(escapePos);
        int toEscapeLen = end - escapePos;
        byte[] temp = stream.borrowTemp(toEscapeLen);
        System.arraycopy(buf, escapePos, temp, 0, toEscapeLen);
        for (int i = 0; i < toEscapeLen; i++) {
            byte b = temp[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                builder.append('\\', '/', (char) ('A' + (b >>> 4)), (char)('A' + (b & 0xF)));
            } else {
                builder.append(b);
            }
        }
        stream.releaseTemp(temp);
        builder.append('"');
    }

    private static int shouldEscape(byte[] buf, int offset, int end) {
        for (int i = offset; i < end; i++) {
            byte b = buf[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                return i;
            }
        }
        return -1;
    }
}