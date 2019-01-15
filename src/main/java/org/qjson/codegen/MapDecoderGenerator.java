package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.codegen.gen.Indent;
import org.qjson.codegen.gen.Line;
import org.qjson.decode.BytesDecoderSource;
import org.qjson.encode.CurrentPath;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;
import org.qjson.spi.QJsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapDecoderGenerator implements Generator {

    public final static Set<Class> VALID_KEY_CLASSES = new HashSet<Class>() {{
        add(String.class);
        add(Character.class);
        add(byte[].class);
        add(Byte.class);
        add(Short.class);
        add(Integer.class);
        add(Long.class);
        add(Float.class);
        add(Double.class);
    }};

    @Override
    public Map<String, Object> args(Codegen.Config cfg, QJsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        Decoder keyDecoder = getKeyDecoder(spi, typeArgs);
        Decoder valueDecoder = getValueDecoder(spi, typeArgs);
        return new HashMap<String, Object>(){{
            put("keyDecoder", keyDecoder);
            put("valueDecoder", valueDecoder);
        }};
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" keyDecoder;"));
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" valueDecoder;"));
    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {
        g.__("this.keyDecoder = ("
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(")args.get(\"keyDecoder\");"));
        g.__("this.valueDecoder = ("
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(")args.get(\"valueDecoder\");"));
    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
        g.__(Helper.class.getCanonicalName()).__(new Line(".expectMapHead(source);"));
        g.__("java.util.Map map = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        // if map is {}
        g.__("if (source.peek() == '}') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__(new Line("return map;"));
        })).__(new Line("}"));
        // fill map
        g.__(Helper.class.getCanonicalName()).__(new Line(".fill(source, keyDecoder, valueDecoder, map);"));
        g.__(new Line("return map;"));
    }

    private static Decoder getKeyDecoder(QJsonSpi spi, Map<TypeVariable, Type> typeArgs) {
        TypeVariable typeParam = Map.class.getTypeParameters()[0];
        Type keyType = SubstituteTypeVariable.$(typeParam, typeArgs);
        if (Object.class.equals(keyType)) {
            keyType = String.class;
        }
        Decoder keyDecoder = spi.decoderOf(keyType);
        if (VALID_KEY_CLASSES.contains(keyType)) {
            return keyDecoder;
        }
        return source -> {
            byte[] bytes = source.decodeBytes();
            BytesDecoderSource newSource = new BytesDecoderSource(bytes, 0, bytes.length);
            return keyDecoder.decode(newSource);
        };
    }

    private static Decoder getValueDecoder(QJsonSpi spi, Map<TypeVariable, Type> typeArgs) {
        TypeVariable typeParam = Map.class.getTypeParameters()[1];
        Type valueType = SubstituteTypeVariable.$(typeParam, typeArgs);
        Decoder valueDecoder = spi.decoderOf(valueType);
        return valueDecoder;
    }

    public interface Helper {

        static void expectMapHead(DecoderSource source) {
            byte b = source.peek();
            if (b != '{') {
                throw source.reportError("expect {");
            }
            source.next();
        }

        static void fill(DecoderSource source, Decoder keyDecoder, Decoder valueDecoder, Map map) {
            byte b;
            do {
                String keyStr = source.decodeString();
                Object key = source.decodeObject(keyDecoder);
                if (source.read() != ':') {
                    throw source.reportError("expect :");
                }
                CurrentPath currentPath = source.currentPath();
                int oldPath = currentPath.enterMapValue(keyStr);
                Object value = source.decodeObject(valueDecoder);
                currentPath.exit(oldPath);
                map.put(key, value);
            } while ((b = source.read()) == ',');
            if (b != '}') {
                throw source.reportError("expect }");
            }
        }
    }
}
