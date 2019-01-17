package org.qjson.any;

import org.junit.Assert;
import org.junit.Test;

public class AnyTest {

    @Test
    public void path_get() {
        AnyMap any = new AnyMap("a", new AnyList(new AnyList(false)));
        Assert.assertFalse((Boolean) any.get("a", 0, 0));
    }
}
