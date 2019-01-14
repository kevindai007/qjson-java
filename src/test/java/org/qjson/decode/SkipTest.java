package org.qjson.decode;

import org.qjson.test.md.TestFramework;
import org.junit.Test;

import static org.qjson.test.md.TestFramework.stripQuote;

public class SkipTest {

    @Test
    public void skip() {
        TestFramework.assertTrue(code -> {
            BytesDecoderSource source = new BytesDecoderSource(null, stripQuote(code) + "true");
            source.skip();
            return source.decodeBoolean();
        });
    }
}
