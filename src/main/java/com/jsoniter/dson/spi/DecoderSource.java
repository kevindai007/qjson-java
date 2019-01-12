package com.jsoniter.dson.spi;

import com.jsoniter.dson.decode.DsonDecodeException;

public interface DecoderSource {
    boolean decodeNull();
    boolean decodeBoolean();
    int decodeInt();
    long decodeLong();
    double decodeDouble();
    String decodeString();
    byte[] decodeBytes();
    byte next();
    DsonDecodeException reportError(String errMsg);
    DsonDecodeException reportError(String errMsg, Exception cause);
}
