package org.qjson;

import org.qjson.test.md.FluentAPI;
import org.junit.Test;

import static org.qjson.test.md.TestFramework.stripQuote;
import static org.qjson.test.md.TestFramework.testDataFromMySection;

public class DecodeEncodeObjectTest {

    @Test
    public void decode_object() {
        QJSON qjson = new QJSON();
        FluentAPI testData = testDataFromMySection();
        testData.assertTrue(encoded -> {
            encoded = stripQuote(encoded);
            Object decoded = qjson.decode(Object.class, encoded);
            String encodedBack = qjson.encode(decoded);
            return encoded.equals(encodedBack);
        });
    }
}
