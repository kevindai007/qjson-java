package com.jsoniter.dson.encode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class EncodePrimitiveTest {

    @Test
    public void type_int() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesStream stream = new BytesStream();
            stream.encodeInt(Integer.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_long() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesStream stream = new BytesStream();
            stream.encodeLong(Long.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_double() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesStream stream = new BytesStream();
            stream.encodeDouble(Double.valueOf(stripQuote(row.get(0))));
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }

    @Test
    public void type_string() {
        for (List<String> row : testDataFromMySection().table().body) {
            BytesStream stream = new BytesStream();
            String input = stripQuote(row.get(0));
            if (input.startsWith("0x")) {
                char c = (char) Long.parseLong(input.substring(2), 16);
                input = new String(new char[]{c});
            }
            stream.encodeString(input);
            Assert.assertEquals(row.get(0), stripQuote(row.get(1)), stream.toString());
        }
    }
}
