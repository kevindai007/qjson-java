package org.qjson.encode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

interface EncodeString {

    static void $(BytesEncoderSink sink, String val) {
        BytesBuilder builder = sink.bytesBuilder();
        builder.append('"');
        // one char translated to 4 bytes "\/AA" in worst case
        int maxSize = 4 * val.length();
        builder.ensureCapacity(builder.length() + maxSize);
        int offset = builder.length();
        byte[] buf = builder.getBuffer();
        ByteBuffer byteBuf = ByteBuffer.wrap(buf, offset, maxSize);
        CoderResult result = StandardCharsets.UTF_8.newEncoder().encode(
                CharBuffer.wrap(val),
                byteBuf, true);
        if (result.isError()) {
            try {
                result.throwException();
            } catch (Exception e) {
                throw sink.reportError("encode string to utf8 failed", e);
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
        byte[] temp = sink.borrowTemp(toEscapeLen);
        System.arraycopy(buf, escapePos, temp, 0, toEscapeLen);
        for (int i = 0; i < toEscapeLen; i++) {
            byte b = temp[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                builder.appendEscape(b);
            } else {
                builder.append(b);
            }
        }
        sink.releaseTemp(temp);
        builder.append('"');
    }

    static int shouldEscape(byte[] buf, int offset, int end) {
        for (int i = offset; i < end; i++) {
            byte b = buf[i];
            boolean isControlCharacter = 0 <= b && b < 0x20;
            if (isControlCharacter || b == '\\' || b == '/' || b == '"') {
                return i;
            }
            // because the byte[] is converted from string
            // we can assume it is valid unicode
        }
        return -1;
    }

    static void $(StringEncoderSink sink, String val) {
        StringBuilder builder = sink.stringBuilder();
        builder.append('"');
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            boolean isControlCharacter = c < 0x20;
            if (isControlCharacter || c == '\\' || c == '/' || c == '"') {
                Append.escape(builder, (byte) c);
            } else {
                builder.append(c);
            }
        }
        builder.append('"');
    }
}
