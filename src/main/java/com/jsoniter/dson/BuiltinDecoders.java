package com.jsoniter.dson;

import com.jsoniter.dson.spi.Decoder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

interface BuiltinDecoders {
    static Map<Type, Decoder> $() {
        return new HashMap<Type, Decoder>() {{
            put(boolean.class, source -> source.decodeNull() ? false : source.decodeBoolean());
            put(Boolean.class, source -> source.decodeNull() ? null : source.decodeBoolean());
            put(byte.class, source -> source.decodeNull() ? (byte) 0 : (short) source.decodeInt());
            put(Byte.class, source -> source.decodeNull() ? null : (short) source.decodeInt());
            put(short.class, source -> source.decodeNull() ? (short) 0 : (short) source.decodeInt());
            put(Short.class, source -> source.decodeNull() ? null : (short) source.decodeInt());
            put(int.class, source -> source.decodeNull() ? 0 : source.decodeInt());
            put(Integer.class, source -> source.decodeNull() ? null : source.decodeInt());
            put(long.class, source -> source.decodeNull() ? 0L : source.decodeLong());
            put(Long.class, source -> source.decodeNull() ? null : source.decodeLong());
            put(float.class, source -> source.decodeNull() ? 0.0F : (float) source.decodeDouble());
            put(Float.class, source -> source.decodeNull() ? null : (float) source.decodeDouble());
            put(double.class, source -> source.decodeNull() ? 0.0D : source.decodeDouble());
            put(Double.class, source -> source.decodeNull() ? null : source.decodeDouble());
            put(String.class, source -> source.decodeNull() ? null : source.decodeString());
            put(byte[].class, source -> source.decodeNull() ? null : source.decodeBytes());
            put(Byte[].class, source -> {
                if (source.decodeNull()) {
                    return null;
                }
                byte[] bytes = source.decodeBytes();
                Byte[] boxed = new Byte[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    boxed[i] = bytes[i];
                }
                return boxed;
            });
        }};
    }
}
