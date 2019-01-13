package org.qjson;

import com.dexscript.test.framework.FluentAPI;
import org.junit.Test;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodeEncodeObjectTest {

    @Test
    public void decode_object() {
        Dson dson = new Dson();
        FluentAPI testData = testDataFromMySection();
        testData.assertTrue(encoded -> {
            encoded = stripQuote(encoded);
            Object decoded = dson.decode(Object.class, encoded);
            String encodedBack = dson.encode(decoded);
            return encoded.equals(encodedBack);
        });
    }
}
