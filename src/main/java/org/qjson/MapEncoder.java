package org.qjson;

import org.qjson.codegen.MapDecoderGenerator;
import org.qjson.encode.BytesBuilder;
import org.qjson.encode.BytesEncoderSink;
import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MapEncoder implements Encoder {

    private final Map<Class, Encoder> keyEncoderCache = new ConcurrentHashMap<>();
    private final Encoder.Provider spi;

    MapEncoder(Encoder.Provider spi) {
        this.spi = spi;
    }

    @Override
    public void encode(EncoderSink sink, Object val) {
        if (val == null) {
            sink.encodeNull();
            return;
        }
        Map<Object, Object> map = (Map<Object, Object>) val;
        sink.write('{');
        boolean isFirst = true;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sink.write(',');
            }
            Object key = entry.getKey();
            if (key == null) {
                sink.encodeString("null");
            } else {
                Encoder keyEncoder = getKeyEncoder(key.getClass());
                keyEncoder.encode(sink, key);
            }
            sink.write(':');
            sink.encodeObject(entry.getValue());
        }
        sink.write('}');
    }

    private Encoder getKeyEncoder(Class<?> clazz) {
        return keyEncoderCache.computeIfAbsent(clazz, this::generateKeyEncoder);
    }

    private Encoder generateKeyEncoder(Class clazz) {
        Encoder encoder = spi.encoderOf(clazz);
        if (MapDecoderGenerator.VALID_KEY_CLASSES.contains(clazz)) {
            return encoder;
        }
        return (sink, val) -> {
            BytesEncoderSink newSink = new BytesEncoderSink(spi, new BytesBuilder());
            encoder.encode(newSink, val);
            sink.encodeBytes(newSink.copyOfBytes());
        };
    }

}
