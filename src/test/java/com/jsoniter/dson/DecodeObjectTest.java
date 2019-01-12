package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import org.junit.Test;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodeObjectTest {

    @Test
    public void decode_object() {
        DSON dson = new DSON();
        FluentAPI testData = testDataFromMySection();
        testData.assertTrue(encoded -> {
            encoded = stripQuote(encoded);
            Object decoded = dson.decode(Object.class, encoded);
            String encodedBack = dson.encode(decoded);
            return encoded.equals(encodedBack);
        });
    }
}
