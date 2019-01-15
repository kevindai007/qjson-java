package org.qjson.decode;

import org.junit.Test;

import static org.qjson.junit.md.TestInMarkdown.myTestData;
import static org.qjson.junit.md.TestInMarkdown.stripQuote;

public class SkipTest {

    @Test
    public void skip() {
        myTestData().assertTrue(code -> {
            BytesDecoderSource source = new BytesDecoderSource(null, stripQuote(code) + "true");
            source.skip();
            return source.decodeBoolean();
        });
        myTestData().assertTrue(code -> {
            StringDecoderSource source = new StringDecoderSource(null, stripQuote(code) + "true");
            source.skip();
            return source.decodeBoolean();
        });
    }
}
