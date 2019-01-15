package org.qjson.encode;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.qjson.junit.md.TestInMarkdown.stripQuote;
import static org.qjson.junit.md.TestInMarkdown.myTestData;

public class EncodePrimitiveTest {

    @Test
    public void type_int() {
        for (List<String> row : myTestData().table().body) {
            int val = Integer.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = newBytesEncoderSink();
            sink1.encodeInt(val);
            StringEncoderSink sink2 = newStringEncoderSink();
            sink2.encodeInt(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_long() {
        for (List<String> row : myTestData().table().body) {
            long val = Long.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = newBytesEncoderSink();
            sink1.encodeLong(val);
            StringEncoderSink sink2 = newStringEncoderSink();
            sink2.encodeLong(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : myTestData().table().body) {
            double val = Double.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = newBytesEncoderSink();
            sink1.encodeDouble(val);
            StringEncoderSink sink2 = newStringEncoderSink();
            sink2.encodeDouble(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_string() {
        for (List<String> row : myTestData().table().body) {
            String input = stripQuote(row.get(0));
            if (input.startsWith("0x")) {
                char c = (char) Long.parseLong(input.substring(2), 16);
                input = new String(new char[]{c});
            }
            BytesEncoderSink sink1 = newBytesEncoderSink();
            sink1.encodeString(input);
            StringEncoderSink sink2 = newStringEncoderSink();
            sink2.encodeString(input);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_bytes() {
        for (List<String> row : myTestData().table().body) {
            String input = stripQuote(row.get(0));
            input = input.substring(1, input.length() - 1);
            String[] elems = input.split(" ");
            if (input.isEmpty()) {
                elems = new String[0];
            }
            byte[] bytes = new byte[elems.length];
            for (int i = 0; i < elems.length; i++) {
                String elem = elems[i];
                bytes[i] = (byte) Long.parseLong(elem, 16);
            }
            BytesEncoderSink sink1 = newBytesEncoderSink();
            sink1.encodeBytes(bytes);
            StringEncoderSink sink2 = newStringEncoderSink();
            sink2.encodeBytes(bytes);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @NotNull
    private StringEncoderSink newStringEncoderSink() {
        return new StringEncoderSink(null);
    }

    @NotNull
    private BytesEncoderSink newBytesEncoderSink() {
        return new BytesEncoderSink(null);
    }
}
