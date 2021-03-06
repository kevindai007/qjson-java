package org.qjson;

import org.qjson.spi.Encoder;

import java.util.HashMap;
import java.util.Map;

interface BuiltinEncoders {
    static Map<Class, Encoder> $(Encoder.Provider spi) {
        return new HashMap<Class, Encoder>() {{
            put(boolean.class, (sink, val) -> sink.encodeBoolean((Boolean) val));
            put(Boolean.class, (sink, val) -> sink.encodeBoolean((Boolean) val));
            put(byte.class, (sink, val) -> sink.encodeInt((Byte) val));
            put(Byte.class, (sink, val) -> sink.encodeInt((Byte) val));
            put(short.class, (sink, val) -> sink.encodeInt((Short) val));
            put(Short.class, (sink, val) -> sink.encodeInt((Short) val));
            put(int.class, (sink, val) -> sink.encodeInt((Integer) val));
            put(Integer.class, (sink, val) -> sink.encodeInt((Integer) val));
            put(long.class, (sink, val) -> sink.encodeLong((Long) val));
            put(Long.class, (sink, val) -> sink.encodeLong((Long) val));
            put(char.class, (sink, val) -> sink.encodeString(new String(new char[]{(Character) val})));
            put(Character.class, (sink, val) -> sink.encodeString(new String(new char[]{(Character) val})));
            put(String.class, (sink, val) -> sink.encodeString((String) val));
            put(float.class, (sink, val) -> sink.encodeDouble((Float) val));
            put(Float.class, (sink, val) -> sink.encodeDouble((Float) val));
            put(double.class, (sink, val) -> sink.encodeDouble((Double) val));
            put(Double.class, (sink, val) -> sink.encodeDouble((Double) val));
            put(byte[].class, (sink, val) -> sink.encodeBytes((byte[]) val));
            put(Byte[].class, (sink, val) -> {
                Byte[] boxed = (Byte[]) val;
                byte[] bytes = new byte[boxed.length];
                for (int i = 0; i < boxed.length; i++) {
                    bytes[i] = boxed[i];
                }
                sink.encodeBytes(bytes);
            });
            put(Map.class, new MapEncoder(spi));
            put(Iterable.class, new IterableEncoder(spi));
        }};
    }
}
