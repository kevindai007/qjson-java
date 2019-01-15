package org.qjson;

import org.qjson.junit.md.TestData;
import org.junit.Test;

import static org.qjson.junit.md.TestInMarkdown.stripQuote;
import static org.qjson.junit.md.TestInMarkdown.myTestData;

public class DecodeEncodeObjectTest {

    @Test
    public void decode_object() {
        QJSON qjson = new QJSON();
        TestData testData = myTestData();
        testData.assertTrue(encoded -> {
            encoded = stripQuote(encoded);
            Object decoded = qjson.decode(Object.class, encoded);
            String encodedBack = qjson.encode(decoded);
            return encoded.equals(encodedBack);
        });
    }
}
