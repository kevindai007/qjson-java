package org.qjson.decode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodePrimitiveTest {

    @Test
    public void type_int() {
        for (List<String> row : testDataFromMySection().table().body) {
            int expected = Integer.valueOf(stripQuote(row.get(0)));
            int actual = new BytesDecoderSource(stripQuote(row.get(1)).getBytes()).decodeInt();
            Assert.assertEquals(row.get(0), expected, actual);
        }
    }

    @Test
    public void type_long() {
        for (List<String> row : testDataFromMySection().table().body) {
            long expected = Long.valueOf(stripQuote(row.get(0)));
            long actual = (Long)new BytesDecoderSource(stripQuote(row.get(1)).getBytes()).decodeStringOrNumber();
            Assert.assertEquals(row.get(0), expected, actual);
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : testDataFromMySection().table().body) {
            String expected = stripQuote(row.get(0));
            String actual = String.valueOf(new BytesDecoderSource(stripQuote(row.get(1)).getBytes()).decodeStringOrNumber());
            Assert.assertEquals(row.get(0), expected, actual);
        }
    }

    @Test
    public void type_string() {
        for (List<String> row : testDataFromMySection().table().body) {
            String expected = stripQuote(row.get(0));
            if (expected.startsWith("0x")) {
                char c = (char) Long.parseLong(expected.substring(2), 16);
                expected = new String(new char[]{c});
            }
            String actual = new BytesDecoderSource(stripQuote(row.get(1)).getBytes()).decodeString();
            Assert.assertEquals(row.get(0), expected, actual);
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
            byte[] expected = new byte[elems.length];
            for (int i = 0; i < elems.length; i++) {
                String elem = elems[i];
                expected[i] = (byte) Long.parseLong(elem, 16);
            }
            byte[] actual = new BytesDecoderSource(stripQuote(row.get(1)).getBytes()).decodeBytes();
            Assert.assertArrayEquals(row.get(0), expected, actual);
        }
    }
}
