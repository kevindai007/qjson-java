package org.qjson.decode;

import org.qjson.encode.BytesBuilder;

interface DecodeBytes {

    static byte[] $(BytesDecoderSource source) {
        source.expect('"');
        int i = source.offset;
        for (; i < source.size; i++) {
            byte b = source.buf[i];
            if (b == '"') {
                byte[] decoded = new byte[i - source.offset];
                System.arraycopy(source.buf, source.offset, decoded, 0, decoded.length);
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
        BytesBuilder temp = source.borrowTemp(noEscapeLen + 16);
        System.arraycopy(source.buf, source.offset, temp.buf(), 0, noEscapeLen);
        temp.setLength(noEscapeLen);
        source.offset = i;
        while (true) {
            if (DecodeString.readEscaped(source, temp)) {
                break;
            }
            if (DecodeString.readRaw(source, temp)) {
                break;
            }
        }
        byte[] decoded = temp.copyOfBytes();
        source.releaseTemp(temp);
        return decoded;
    }
}
