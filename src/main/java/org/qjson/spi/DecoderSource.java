package org.qjson.spi;

import org.qjson.decode.QJsonDecodeException;

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
    QJsonDecodeException reportError(String errMsg);
    QJsonDecodeException reportError(String errMsg, Exception cause);
}
