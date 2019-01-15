package org.qjson;

import org.qjson.any.AnyMap;
import org.qjson.codegen.Codegen;
import org.qjson.decode.BytesDecoderSource;
import org.qjson.decode.QJsonDecodeException;
import org.qjson.encode.CurrentPath;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;
import org.qjson.spi.TypeVariables;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class MapDecoder implements Decoder {

    private final Function<DecoderSource, Map> mapFactory;
    private final Decoder keyDecoder;
    private final Decoder valueDecoder;

    public MapDecoder(Function<DecoderSource, Map> mapFactory, Decoder keyDecoder, Decoder valueDecoder) {
        this.mapFactory = mapFactory;
        this.keyDecoder = keyDecoder;
        this.valueDecoder = valueDecoder;
    }

    static MapDecoder create(Codegen.Config cfg, Decoder.Provider spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        Function<DecoderSource, Map> mapFactory = getMapFactory(clazz);
        Decoder keyDecoder = getKeyDecoder(spi, typeArgs);
        Decoder valueDecoder = getValueDecoder(spi, typeArgs);
        return new MapDecoder(mapFactory, keyDecoder, valueDecoder);
    }

    private static Function<DecoderSource, Map> getMapFactory(Class clazz) {
        if (clazz.equals(AnyMap.class)) {
            return source -> new AnyMap();
        }
        if (clazz.equals(HashMap.class)) {
            return source -> new HashMap();
        }
        try {
            Constructor ctor = clazz.getConstructor();
            return source -> {
                try {
                    return (Map) ctor.newInstance();
                } catch (Exception e) {
                    throw source.reportError("create map failed", e);
                }
            };

        } catch (NoSuchMethodException e) {
            throw new QJsonDecodeException("no default constructor for: " + clazz, e);
        }
    }

    private static Decoder getKeyDecoder(Decoder.Provider spi, Map<TypeVariable, Type> typeArgs) {
        TypeVariable typeParam = Map.class.getTypeParameters()[0];
        Type keyType = TypeVariables.substitute(typeParam, typeArgs);
        if (Object.class.equals(keyType)) {
            keyType = String.class;
        }
        Decoder keyDecoder = spi.decoderOf(keyType);
        if (MapEncoder.VALID_KEY_CLASSES.contains(keyType)) {
            return keyDecoder;
        }
        return source -> {
            byte[] bytes = source.decodeBytes();
            BytesDecoderSource newSource = new BytesDecoderSource(bytes, 0, bytes.length);
            return keyDecoder.decode(newSource);
        };
    }

    private static Decoder getValueDecoder(Decoder.Provider spi, Map<TypeVariable, Type> typeArgs) {
        TypeVariable typeParam = Map.class.getTypeParameters()[1];
        Type valueType = TypeVariables.substitute(typeParam, typeArgs);
        Decoder valueDecoder = spi.decoderOf(valueType);
        return valueDecoder;
    }

    @Override
    public Object decode(DecoderSource source) {
        byte b = source.peek();
        if (b != '{') {
            throw source.reportError("expect {");
        }
        source.next();
        Map map = mapFactory.apply(source);
        // if map is {}
        if (source.peek() == '}') {
            source.next();
            return map;
        }
        do {
            int mark = source.mark();
            Object key = source.decodeObject(keyDecoder);
            String encodedKey = source.sinceMark(mark);
            if (source.read() != ':') {
                throw source.reportError("expect :");
            }
            CurrentPath currentPath = source.currentPath();
            int oldPath = currentPath.enterMapValue(encodedKey);
            Object value = source.decodeObject(valueDecoder);
            currentPath.exit(oldPath);
            map.put(key, value);
        } while ((b = source.read()) == ',');
        if (b != '}') {
            throw source.reportError("expect }");
        }
        return map;
    }
}
