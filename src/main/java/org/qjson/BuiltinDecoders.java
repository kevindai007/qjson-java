package org.qjson;

import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

interface BuiltinDecoders {
    static Map<Type, Decoder> $() {
        return new HashMap<Type, Decoder>() {{
            put(boolean.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return source.decodeBoolean();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return false;
                }
            });
            put(Boolean.class, DecoderSource::decodeBoolean);
            put(byte.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return (byte) source.decodeInt();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return (byte) 0;
                }
            });
            put(Byte.class, source -> (byte) source.decodeInt());
            put(short.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return (short) source.decodeInt();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return (short) 0;
                }
            });
            put(Short.class, source -> (short) source.decodeInt());
            put(int.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return source.decodeInt();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return 0;
                }
            });
            put(Integer.class, DecoderSource::decodeInt);
            put(long.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return source.decodeLong();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return 0L;
                }
            });
            put(Long.class, DecoderSource::decodeLong);
            put(float.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return (float) source.decodeDouble();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return 0.0F;
                }
            });
            put(Float.class, source -> (float)source.decodeDouble());
            put(double.class, new Decoder() {
                @Override
                public Object decode(DecoderSource source) {
                    return source.decodeDouble();
                }

                @Override
                public Object decodeNull(DecoderSource source) {
                    return 0.0D;
                }
            });
            put(Double.class, DecoderSource::decodeDouble);
            put(String.class, DecoderSource::decodeString);
            put(byte[].class, DecoderSource::decodeBytes);
            put(Byte[].class, source -> {
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
