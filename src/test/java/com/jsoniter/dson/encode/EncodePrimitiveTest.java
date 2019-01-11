package com.jsoniter.dson.encode;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class EncodePrimitiveTest {

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
}
