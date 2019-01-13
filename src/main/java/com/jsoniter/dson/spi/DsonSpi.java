package com.jsoniter.dson.spi;

import java.lang.reflect.Type;

public interface DsonSpi {
    Encoder encoderOf(Class clazz);
    Decoder decoderOf(Type type);
}
