package org.qjson.encode;

import org.qjson.spi.Encoder;
import org.qjson.spi.EncoderSink;

import java.util.HashMap;
import java.util.Map;

final class PathTracker {

    private final Map<Integer, String> visited = new HashMap<>();
    private CurrentPath currentPath = new CurrentPath();
    private final EncoderSink sink;

    PathTracker(EncoderSink sink) {
        this.sink = sink;
    }

    public CurrentPath currentPath() {
        return currentPath;
    }

    public final void encodeObject(Object val, Encoder encoder) {
        if (val == null) {
            encoder.encodeNull(sink);
            return;
        }
        encode(val, encoder);
    }

    public final void encodeObject(Object val, Encoder.Provider spi) {
        if (val == null) {
            sink.encodeNull();
            return;
        }
        Encoder encoder = spi.encoderOf(val.getClass());
        encode(val, encoder);
    }

    private void encode(Object val, Encoder encoder) {
        int id = System.identityHashCode(val);
        String ref = visited.get(id);
        if (ref == null) {
            visited.put(id, currentPath.toString());
            encoder.encode(sink, val);
        } else {
            encoder.encodeRef(sink, val, ref);
        }
    }

    public void reset() {
        visited.clear();
        currentPath.exit(0);
    }
}
