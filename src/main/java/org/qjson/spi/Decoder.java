package org.qjson.spi;

import java.lang.reflect.Type;

public interface Decoder {

    interface Provider {
        Decoder decoderOf(Type type);
    }

    Object decode(DecoderSource source);
}
