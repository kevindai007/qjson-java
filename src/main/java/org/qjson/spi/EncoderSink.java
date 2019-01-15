package org.qjson.spi;

import org.qjson.encode.CurrentPath;
import org.qjson.encode.QJsonEncodeException;

public interface EncoderSink {

    void encodeNull();

    void encodeBoolean(boolean val);

    void encodeInt(int val);

    void encodeLong(long val);

    void encodeDouble(double val);

    void encodeString(String val);

    void encodeBytes(byte[] val);

    void encodeObject(Object val, Encoder encoder);

    void encodeObject(Object val, Encoder.Provider spi);

    CurrentPath currentPath();

    void encodeRef(String ref);

    void write(String raw);

    void write(char b);

    QJsonEncodeException reportError(String errMsg);

    QJsonEncodeException reportError(String errMsg, Exception cause);

    <T> T borrowTemp(Class<T> clazz);

    void releaseTemp(Object temp);
}
