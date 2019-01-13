package org.qjson.spi;

public interface Encoder {
    void encode(EncoderSink sink, Object val);
}
