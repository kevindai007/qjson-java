package org.qjson.spi;

import java.lang.reflect.Type;

public interface Decoder {

    interface Provider {
        Decoder decoderOf(Type type);
    }

    Object decode(DecoderSource source);

    default void decodeProperties(DecoderSource source, Object obj) {
    }

    default Object decodeNull(DecoderSource source) {
        return null;
    }

    default Object decodeRef(DecoderSource source, String path, Object ref) {
        return ref;
    }
}
