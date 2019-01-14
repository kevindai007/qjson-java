package org.qjson.decode;

import org.junit.Assert;
import org.junit.Test;

public class BytesDecoderSourceTest {

    @Test
    public void decode_boolean() {
        BytesDecoderSource source = new BytesDecoderSource("true");
        Assert.assertTrue(source.decodeBoolean());
        source = new BytesDecoderSource("false");
        Assert.assertFalse(source.decodeBoolean());
    }

    @Test
    public void decode_null() {
        BytesDecoderSource source = new BytesDecoderSource("\"\"");
        Assert.assertFalse(source.decodeNull());
        source = new BytesDecoderSource("null");
        Assert.assertTrue(source.decodeNull());
    }
}
