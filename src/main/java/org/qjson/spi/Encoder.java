package org.qjson.spi;

public interface Encoder {

    interface Provider {
        Encoder encoderOf(Class clazz);
    }

    void encode(EncoderSink sink, Object val);
}
