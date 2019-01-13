package com.jsoniter.dson;

import com.jsoniter.dson.any.Any;
import org.junit.Assert;
import org.junit.Test;

public class AnyTest {

    @Test
    public void decode_any() {
        Assert.assertEquals("b", DSON.decode("[{\"a\":\"b\"}]").get(0, "a"));
        Assert.assertEquals(true, DSON.decode("true").get());
    }

    @Test
    public void encode_any() {
        Any any = DSON.decode("[{\"a\":\"b\"}]");
        any.at(0).set("c", "d");
        Assert.assertEquals("[{\"a\":\"b\",\"c\":\"d\"}]", DSON.$.encode(any));
    }
}
