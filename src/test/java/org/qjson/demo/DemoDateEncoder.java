package org.qjson.demo;

import org.qjson.QJSON;
import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.time.*;
import java.util.Date;

public class DemoDateEncoder implements Encoder {

    @Override
    public void encode(EncoderSink sink, Object val) {
        Date date = (Date) val;
        sink.encodeLong(date.getTime());
    }

    public static void demo() {
        QJSON.Config cfg = new QJSON.Config();
        cfg.chooseEncoder = (qjson, clazz) -> {
            if (Date.class.equals(clazz)) {
                return new DemoDateEncoder();
            }
            return null;
        };
        QJSON qjson = new QJSON(cfg);
        OffsetDateTime date = OffsetDateTime.of(LocalDateTime.of(
                2008, 8, 8, 0, 0), ZoneOffset.UTC);
        System.out.println(qjson.encode(date));
    }
}
