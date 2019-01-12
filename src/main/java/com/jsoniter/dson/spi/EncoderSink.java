package com.jsoniter.dson.spi;

import com.jsoniter.dson.encode.DsonEncodeException;

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
    DsonEncodeException reportError(String errMsg);
    DsonEncodeException reportError(String errMsg, Exception cause);
}
