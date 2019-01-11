package com.jsoniter.dson.decode;

public interface Iterator {
    boolean decodeNull();
    boolean decodeBoolean();
    int decodeInt();
    long decodeLong();
    double decodeDouble();
    String decodeString();
    byte[] decodeBytes();
}
