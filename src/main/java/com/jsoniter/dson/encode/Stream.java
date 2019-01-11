package com.jsoniter.dson.encode;

interface Stream {
    void encodeLong(long val);
    void encodeDouble(double val);
    void encodeString(String val);
    DsonEncodeException reportError(String errMsg, Exception cause);
}
