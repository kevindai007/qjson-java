package org.qjson.spi;

import java.lang.reflect.Type;

public interface QJsonSpi {
    Encoder encoderOf(Class clazz);
    Decoder decoderOf(Type type);
}
