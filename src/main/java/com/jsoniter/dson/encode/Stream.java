package com.jsoniter.dson.encode;

interface Stream {
    void encodeNull();
    void encodeBoolean(boolean val);
    void encodeInt(int val);
    void encodeLong(long val);
    void encodeDouble(double val);
    void encodeString(String val);
    void encodeBytes(byte[] val);
    void encodeObject(Object val);
    void write(byte b);
    DsonEncodeException reportError(String errMsg);
    DsonEncodeException reportError(String errMsg, Exception cause);
}
