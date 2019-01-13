package com.jsoniter.dson;

import com.jsoniter.dson.any.Any;
import org.junit.Assert;
import org.junit.Test;

public class AnyTest {

    @Test
    public void decode_any() {
        Assert.assertEquals("b", Dson.decode("[{\"a\":\"b\"}]").get(0, "a"));
        Assert.assertEquals(true, Dson.decode("true").get());
    }

    @Test
    public void encode_any() {
        Any any = Dson.decode("[{\"a\":\"b\"}]");
        any.at(0).set("c", "d");
        Assert.assertEquals("[{\"a\":\"b\",\"c\":\"d\"}]", Dson.$.encode(any));
    }
}
