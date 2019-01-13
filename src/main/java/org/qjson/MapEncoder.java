package org.qjson;

import org.qjson.codegen.MapDecoderGenerator;
import org.qjson.encode.BytesBuilder;
import org.qjson.encode.BytesEncoderSink;
import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class MapEncoder implements Encoder {

    private final Map<Class, Encoder> keyEncoderCache = new ConcurrentHashMap<>();
    private final Function<Class, Encoder> encoderProvider;

    MapEncoder(Function<Class, Encoder> encoderProvider) {
        this.encoderProvider = encoderProvider;
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
        Encoder encoder = encoderProvider.apply(clazz);
        if (MapDecoderGenerator.VALID_KEY_CLASSES.contains(clazz)) {
            return encoder;
        }
        return (sink, val) -> {
            BytesEncoderSink newSink = new BytesEncoderSink(encoderProvider, new BytesBuilder());
            encoder.encode(newSink, val);
            sink.encodeBytes(newSink.copyOfBytes());
        };
    }

}
