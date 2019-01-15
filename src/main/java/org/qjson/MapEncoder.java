package org.qjson;

import org.qjson.codegen.MapDecoderGenerator;
import org.qjson.encode.CurrentPath;
import org.qjson.encode.StringEncoderSink;
import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

class MapEncoder implements Encoder {

    private final Map<Class, BiFunction<EncoderSink, Object,String>> keyEncoderCache = new ConcurrentHashMap<>();
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
            String encodedKey;
            if (key == null) {
                encodedKey = "\"null\"";
            } else {
                BiFunction<EncoderSink, Object, String> keyEncoder = getKeyEncoder(key.getClass());
                encodedKey = keyEncoder.apply(sink, key);
            }
            sink.write(encodedKey);
            sink.write(':');
            CurrentPath currentPath = sink.currentPath();
            int oldPath = currentPath.enterMapValue(encodedKey);
            sink.encodeObject(entry.getValue(), spi);
            currentPath.exit(oldPath);
        }
        sink.write('}');
    }

    private BiFunction<EncoderSink, Object, String> getKeyEncoder(Class<?> clazz) {
        return keyEncoderCache.computeIfAbsent(clazz, this::generateKeyEncoder);
    }

    private BiFunction<EncoderSink, Object, String> generateKeyEncoder(Class clazz) {
        Encoder encoder = spi.encoderOf(clazz);
        boolean isValidKeyClass = MapDecoderGenerator.VALID_KEY_CLASSES.contains(clazz);
        return (sink, val) -> {
            StringEncoderSink newSink = sink.borrowTemp(StringEncoderSink.class);
            if (newSink == null) {
                newSink = new StringEncoderSink();
            }
            newSink.reset();
            if (isValidKeyClass) {
                encoder.encode(newSink, val);
            } else {
                newSink.write('"');
                encoder.encode(newSink, val);
                newSink.write('"');
            }
            String encodedKey = newSink.toString();
            newSink.reset();
            sink.releaseTemp(newSink);
            return encodedKey;
        };
    }
}
