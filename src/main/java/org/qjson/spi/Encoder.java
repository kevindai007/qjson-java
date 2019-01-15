package org.qjson.spi;

public interface Encoder {

    interface Provider {
        Encoder encoderOf(Class clazz);
    }

    void encode(EncoderSink sink, Object val);

    default void encodeNull(EncoderSink sink) {
        sink.encodeNull();
    }

    default void encodeRef(EncoderSink sink, Object val, String ref) {
        sink.encodeRef(ref);
    }
}
