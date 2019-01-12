package com.jsoniter.dson;

import com.jsoniter.dson.spi.EncoderSink;

import java.util.Map;

interface EncodeMap {
    static void $(EncoderSink sink, Map<Object, Object> map) {
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
