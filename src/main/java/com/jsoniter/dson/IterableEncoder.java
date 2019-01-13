package com.jsoniter.dson;

import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;

class IterableEncoder implements Encoder {
    @Override
    public void encode(EncoderSink sink, Object val) {
        if (val == null) {
            sink.encodeNull();
            return;
        }
        Iterable iterable = (Iterable) val;
        sink.write('[');
        boolean isFirst = true;
        for (Object elem : iterable) {
            if (isFirst) {
                isFirst = false;
            } else {
                sink.write(',');
            }
            sink.encodeObject(elem);
        }
        sink.write(']');
    }
}
