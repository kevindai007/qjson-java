package com.jsoniter.dson.decode;

public interface Iterator {
    int decodeInt();
    long decodeLong();
    double decodeDouble();
    String decodeString();
    boolean decodeBoolean();
    boolean decodeNull();
}
