package com.jsoniter.dson.spi;

import com.jsoniter.dson.decode.DsonDecodeException;

public interface DecoderSource {
    boolean decodeNull();
    boolean decodeBoolean();
    int decodeInt();
    long decodeLong();
    double decodeDouble();
    String decodeString();
    Object decodeStringOrNumber();
    byte[] decodeBytes();
    <T> T decodeObject(Class<T> clazz);
    byte peek();
    void next();
    DsonDecodeException reportError(String errMsg);
    DsonDecodeException reportError(String errMsg, Exception cause);
}
