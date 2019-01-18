package org.qjson.demo.inf;

import org.junit.Test;
import org.qjson.QJSON;
import org.qjson.demo.TestDemo;

public class inf {
    @Test
    public void encode() {
        QJSON.Config cfg = new QJSON.Config();
        cfg.chooseEncoder = (qjson, clazz) -> {
            if (!My.Inf.class.isAssignableFrom(clazz)) {
                return null;
            }
            return qjson.encoderOf(My.Inf.class);
        };
        TestDemo.$();
    }
}
