package com.jsoniter.dson.decode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodePrimitiveTest {

    @Test
    public void type_long() {
        for (List<String> row : testDataFromMySection().table().body) {
            long expected = Long.valueOf(stripQuote(row.get(0)));
            long actual = new BytesIterator(stripQuote(row.get(1)).getBytes()).decodeLong();
            Assert.assertEquals(row.get(0), expected, actual);
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : testDataFromMySection().table().body) {
            String expected = stripQuote(row.get(0));
            String actual = String.valueOf(new BytesIterator(stripQuote(row.get(1)).getBytes()).decodeDouble());
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
            String actual = new BytesIterator(stripQuote(row.get(1)).getBytes()).decodeString();
            Assert.assertEquals(row.get(0), expected, actual);
        }
    }
}
