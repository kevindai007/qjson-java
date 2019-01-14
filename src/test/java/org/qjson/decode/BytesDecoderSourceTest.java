package org.qjson.decode;

import org.junit.Assert;
import org.junit.Test;

public class BytesDecoderSourceTest {

    @Test
    public void decode_boolean() {
        BytesDecoderSource source = new BytesDecoderSource(null, "true");
        Assert.assertTrue(source.decodeBoolean());
        source = new BytesDecoderSource(null, "false");
        Assert.assertFalse(source.decodeBoolean());
    }

    @Test
    public void decode_null() {
        BytesDecoderSource source = new BytesDecoderSource(null, "\"\"");
        Assert.assertFalse(source.decodeNull());
        source = new BytesDecoderSource(null, "null");
        Assert.assertTrue(source.decodeNull());
    }
}
