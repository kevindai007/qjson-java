package com.jsoniter.dson.marshal;

import org.junit.Assert;
import org.junit.Test;

public class BytesStreamTest {

    @Test
    public void encode_int() {
        BytesStream stream = new BytesStream();
        stream.encodeInt(256);
        Assert.assertEquals("\"\"", stream.bytesBuilder().toString());
    }
}
