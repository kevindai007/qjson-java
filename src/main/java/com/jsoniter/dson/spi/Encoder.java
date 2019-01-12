package com.jsoniter.dson.spi;

public interface Encoder {
    void encode(EncoderSink sink, Object val);
}
