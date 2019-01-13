package org.qjson.decode;

import com.dexscript.test.framework.TestFramework;
import org.junit.Test;

import static com.dexscript.test.framework.TestFramework.stripQuote;

public class SkipTest {

    @Test
    public void skip() {
        TestFramework.assertTrue(code -> {
            BytesDecoderSource source = new BytesDecoderSource(stripQuote(code) + "true");
            source.skip();
            return source.decodeBoolean();
        });
    }
}
