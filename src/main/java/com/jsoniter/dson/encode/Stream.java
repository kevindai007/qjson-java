package com.jsoniter.dson.encode;

interface Stream {
    void encodeInt(int val);
    void encodeLong(long val);
    void encodeDouble(double val);
    void encodeString(String val);
    void encodeBoolean(boolean val);
    void encodeNull();
    DsonEncodeException reportError(String errMsg, Exception cause);
}
