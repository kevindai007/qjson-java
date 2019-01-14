package org.qjson.encode;

import org.junit.Assert;
import org.junit.Test;

public class BytesEncoderSinkTest {

    @Test
    public void encode_boolean() {
        BytesEncoderSink sink = new BytesEncoderSink(null);
        sink.encodeBoolean(true);
        Assert.assertEquals("true", sink.toString());
        sink = new BytesEncoderSink(null);
        sink.encodeBoolean(false);
        Assert.assertEquals("false", sink.toString());
    }

    @Test
    public void encode_null() {
        BytesEncoderSink sink = new BytesEncoderSink(null);
        sink.encodeNull();
        Assert.assertEquals("null", sink.toString());
    }
}
