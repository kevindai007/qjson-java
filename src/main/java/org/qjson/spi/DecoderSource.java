package org.qjson.spi;

import org.qjson.decode.DsonDecodeException;

import java.lang.reflect.Type;

public interface DecoderSource {
    boolean decodeNull();
    boolean decodeBoolean();
    int decodeInt();
    long decodeLong();
    double decodeDouble();
    String decodeString();
    Object decodeStringOrNumber();
    byte[] decodeBytes();
    Object decodeObject(Type type);
    byte read();
    byte peek();
    void next();
    void skip();
    DsonDecodeException reportError(String errMsg);
    DsonDecodeException reportError(String errMsg, Exception cause);
}
