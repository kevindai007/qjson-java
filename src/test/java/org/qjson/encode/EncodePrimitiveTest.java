package org.qjson.encode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class EncodePrimitiveTest {

    @Test
    public void type_int() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesEncoderSink stream = new BytesEncoderSink();
            stream.encodeInt(Integer.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
        for (List<String> row : testDataFromMySection().table().body) {
            StringEncoderSink stream = new StringEncoderSink();
            stream.encodeInt(Integer.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_long() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesEncoderSink stream = new BytesEncoderSink();
            stream.encodeLong(Long.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesEncoderSink stream = new BytesEncoderSink();
            stream.encodeDouble(Double.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_string() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesEncoderSink stream = new BytesEncoderSink();
            String input = stripQuote(row.get(0));
            if (input.startsWith("0x")) {
                char c = (char) Long.parseLong(input.substring(2), 16);
                input = new String(new char[]{c});
            }
            stream.encodeString(input);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_bytes() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesEncoderSink stream = new BytesEncoderSink();
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
            stream.encodeBytes(bytes);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }
}
