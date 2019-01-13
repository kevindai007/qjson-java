package org.qjson.decode;

import org.junit.Assert;
import org.junit.Test;

public class BytesDecoderSourceTest {

    @Test
    public void decode_boolean() {
        BytesDecoderSource iter = new BytesDecoderSource("true");
        Assert.assertTrue(iter.decodeBoolean());
        iter = new BytesDecoderSource("false");
        Assert.assertFalse(iter.decodeBoolean());
    }

    @Test
    public void decode_null() {
        BytesDecoderSource iter = new BytesDecoderSource("\"\"");
        Assert.assertFalse(iter.decodeNull());
        iter = new BytesDecoderSource("null");
        Assert.assertTrue(iter.decodeNull());
    }
}
