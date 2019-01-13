package org.qjson.spi;

import org.qjson.encode.QJsonEncodeException;

public interface EncoderSink {
    void encodeNull();
    void encodeBoolean(boolean val);
    void encodeInt(int val);
    void encodeLong(long val);
    void encodeDouble(double val);
    void encodeString(String val);
    void encodeBytes(byte[] val);
    void encodeObject(Object val);
    void write(char b);
    QJsonEncodeException reportError(String errMsg);
    QJsonEncodeException reportError(String errMsg, Exception cause);
}
