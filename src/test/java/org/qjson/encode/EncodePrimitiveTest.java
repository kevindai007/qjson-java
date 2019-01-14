package org.qjson.encode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.qjson.test.md.TestFramework.stripQuote;
import static org.qjson.test.md.TestFramework.testDataFromMySection;

public class EncodePrimitiveTest {

    @Test
    public void type_int() {
        for (List<String> row : testDataFromMySection().table().body) {
            int val = Integer.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = new BytesEncoderSink();
            sink1.encodeInt(val);
            StringEncoderSink sink2 = new StringEncoderSink();
            sink2.encodeInt(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_long() {
        for (List<String> row : testDataFromMySection().table().body) {
            long val = Long.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = new BytesEncoderSink();
            sink1.encodeLong(val);
            StringEncoderSink sink2 = new StringEncoderSink();
            sink2.encodeLong(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : testDataFromMySection().table().body) {
            double val = Double.valueOf(stripQuote(row.get(0)));
            BytesEncoderSink sink1 = new BytesEncoderSink();
            sink1.encodeDouble(val);
            StringEncoderSink sink2 = new StringEncoderSink();
            sink2.encodeDouble(val);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_string() {
        for (List<String> row : testDataFromMySection().table().body) {
            String input = stripQuote(row.get(0));
            if (input.startsWith("0x")) {
                char c = (char) Long.parseLong(input.substring(2), 16);
                input = new String(new char[]{c});
            }
            StringEncoderSink sink2 = new StringEncoderSink();
            sink2.encodeString(input);
            BytesEncoderSink sink1 = new BytesEncoderSink();
            sink1.encodeString(input);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink2.toString());
        }
    }

    @Test
    public void type_bytes() {
        for (List<String> row : testDataFromMySection().table().body) {
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
            BytesEncoderSink sink1 = new BytesEncoderSink();
            sink1.encodeBytes(bytes);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), sink1.toString());
        }
    }
}
