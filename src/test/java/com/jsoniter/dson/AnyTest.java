package com.jsoniter.dson;

import com.jsoniter.dson.any.Any;
import org.junit.Assert;
import org.junit.Test;

public class AnyTest {

    @Test
    public void decode_any() {
        Assert.assertEquals("b", new DSON().decode("[{\"a\":\"b\"}]").get(0, "a"));
        Assert.assertEquals(true, new DSON().decode("true").get());
    }

    @Test
    public void encode_any() {
        DSON dson = new DSON();
        Any any = dson.decode("[{\"a\":\"b\"}]");
        any.at(0).set("c", "d");
        Assert.assertEquals("[{\"a\":\"b\",\"c\":\"d\"}]", dson.encode(any));
    }
}
