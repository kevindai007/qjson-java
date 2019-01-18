package org.qjson.spi;

// Encoder should not be used directly
// always use `sink.encodeObject` to call encoder
public interface Encoder {

    interface Provider {
        Encoder encoderOf(Class clazz);
    }

    // if null: encodeNull will be called
    // if object already encoded: encodeRef will be called
    // otherwise: encode will be called
    void encode(EncoderSink sink, Object val);

    // override encodeNull to encode different value out
    // default behavior is to write out "null"
    default void encodeNull(EncoderSink sink) {
        sink.encodeNull();
    }

    // override encodeRef to force encode out the whole object again
    // default behavior is to write out "\/path" as reference
    default void encodeRef(EncoderSink sink, Object val, String ref) {
        sink.encodeRef(ref);
    }
}
