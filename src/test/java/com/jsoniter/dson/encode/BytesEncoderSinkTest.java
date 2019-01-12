package com.jsoniter.dson.encode;

import org.junit.Assert;
import org.junit.Test;

public class BytesEncoderSinkTest {

    @Test
    public void encode_boolean() {
        BytesEncoderSink stream = new BytesEncoderSink();
        stream.encodeBoolean(true);
        Assert.assertEquals("true", stream.toString());
        stream = new BytesEncoderSink();
        stream.encodeBoolean(false);
        Assert.assertEquals("false", stream.toString());
    }

    @Test
    public void encode_null() {
        BytesEncoderSink stream = new BytesEncoderSink();
        stream.encodeNull();
        Assert.assertEquals("null", stream.toString());
    }
}
