package org.qjson.decode;

import org.qjson.encode.BytesBuilder;

import java.nio.charset.StandardCharsets;

interface DecodeString {

    static String $(BytesDecoderSource source) {
        source.expect('"');
        int i = source.offset;
        for (; i < source.size; i++) {
            byte b = source.buf[i];
            if (b == '"') {
                String decoded = new String(source.buf, source.offset, i - source.offset, StandardCharsets.UTF_8);
                source.offset = i + 1;
                return decoded;
            }
            if (b == '\\') {
                break;
            }
        }
        if (i >= source.size) {
            throw source.reportError("missing double quote");
        }
        int noEscapeLen = i - source.offset;
        byte[] temp = source.borrowTemp(noEscapeLen + 16);
        System.arraycopy(source.buf, source.offset, temp, 0, noEscapeLen);
        BytesBuilder builder = new BytesBuilder(temp, noEscapeLen);
        source.offset = i;
        while (true) {
            if (readEscaped(source, builder)) {
                break;
            }
            if (readRaw(source, builder)) {
                break;
            }
        }
        String decoded = builder.toString();
        source.releaseTemp(builder.getBuffer());
        return decoded;
    }

    static boolean readEscaped(BytesDecoderSource source, BytesBuilder builder) {
        for (int i = source.offset; i < source.size; ) {
            byte b = source.buf[i];
            if (b == '"') {
                source.offset = i + 1;
                return true;
            }
            if (b != '\\') {
                source.offset = i;
                return false;
            }
            if (i + 4 >= source.size) {
                throw source.reportError("missing double quote");
            }
            if (source.buf[i + 1] != '/') {
                throw source.reportError("escape \\/ is the only supported escape form");
            }
            b = (byte) (((source.buf[i + 2] - 'A') << 4) + source.buf[i + 3] - 'A');
            builder.append(b);
            i += 4;
        }
        throw source.reportError("missing double quote");
    }

    static boolean readRaw(BytesDecoderSource source, BytesBuilder builder) {
        for (int i = source.offset; i < source.size; i++) {
            byte b = source.buf[i];
            if (b == '"') {
                source.offset = i + 1;
                return true;
            }
            if (b == '\\') {
                source.offset = i;
                return false;
            }
            builder.append(b);
        }
        throw source.reportError("missing double quote");
    }
}
