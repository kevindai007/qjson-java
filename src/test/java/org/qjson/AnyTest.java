package org.qjson;

import org.qjson.any.Any;
import org.junit.Assert;
import org.junit.Test;

public class AnyTest {

    @Test
    public void decode_any() {
        Assert.assertEquals("b", QJSON.$.decode("[{\"a\":\"b\"}]").get(0, "a"));
        Assert.assertEquals(true, QJSON.$.decode("true").get());
    }

    @Test
    public void encode_any() {
        Any any = QJSON.$.decode("[{\"a\":\"b\"}]");
        any.at(0).set("c", "d");
        Assert.assertEquals("[{\"a\":\"b\",\"c\":\"d\"}]", QJSON.$.encode(any));
    }
}
