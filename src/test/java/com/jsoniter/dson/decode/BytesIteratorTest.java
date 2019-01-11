package com.jsoniter.dson.decode;

import org.junit.Assert;
import org.junit.Test;

public class BytesIteratorTest {

    @Test
    public void decode_boolean() {
        BytesIterator iter = new BytesIterator("true");
        Assert.assertTrue(iter.decodeBoolean());
        iter = new BytesIterator("false");
        Assert.assertFalse(iter.decodeBoolean());
    }

    @Test
    public void decode_null() {
        BytesIterator iter = new BytesIterator("\"\"");
        Assert.assertFalse(iter.decodeNull());
        iter = new BytesIterator("null");
        Assert.assertTrue(iter.decodeNull());
    }
}
