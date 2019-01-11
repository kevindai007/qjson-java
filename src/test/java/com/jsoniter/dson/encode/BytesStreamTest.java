package com.jsoniter.dson.encode;

import org.junit.Assert;
import org.junit.Test;

public class BytesStreamTest {

    @Test
    public void encode_boolean() {
        BytesStream stream = new BytesStream();
        stream.encodeBoolean(true);
        Assert.assertEquals("true", stream.toString());
        stream = new BytesStream();
        stream.encodeBoolean(false);
        Assert.assertEquals("false", stream.toString());
    }

    @Test
    public void encode_null() {
        BytesStream stream = new BytesStream();
        stream.encodeNull();
        Assert.assertEquals("null", stream.toString());
    }
}
