package com.jsoniter.dson;

import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;

import java.util.Map;

class MapEncoder implements Encoder {

    @Override
    public void encode(EncoderSink sink, Object val) {
        Map<Object, Object> map = (Map<Object, Object>) val;
        sink.write('{');
        boolean isFirst = true;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sink.write(',');
            }
            Object key = entry.getKey();
            if (key == null) {
                sink.encodeString("null");
            } else {
                sink.encodeString(key.toString());
            }
            sink.write(':');
            sink.encodeObject(entry.getValue());
        }
        sink.write('}');
    }
}
