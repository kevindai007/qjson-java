package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.decode.BytesDecoderSource;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;
import com.jsoniter.dson.spi.DsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface MapDecoder {

    Set<Class> VALID_KEY_CLASSES = new HashSet<Class>() {{
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

    static void $(Gen g, String decoderClassName, Class clazz) {
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" keyDecoder;"));
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" valueDecoder;"));
        GenDecoder.ctor(g, decoderClassName, new Indent(() -> {
            g.__("this.keyDecoder = "
            ).__(Helper.class.getCanonicalName()
            ).__(new Line(".getKeyDecoder(spi, typeArgs);"));
            g.__("this.valueDecoder = "
            ).__(Helper.class.getCanonicalName()
            ).__(new Line(".getValueDecoder(spi, typeArgs);"));
        }));
        GenDecoder.method(g, new Indent(() -> {
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
        }));
    }

    interface Helper {

        static Decoder getKeyDecoder(DsonSpi spi, Map<TypeVariable, Type> typeArgs) {
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
                BytesDecoderSource newSource = new BytesDecoderSource(spi::decoderOf, bytes, 0, bytes.length);
                return keyDecoder.decode(newSource);
            };
        }

        static Decoder getValueDecoder(DsonSpi spi, Map<TypeVariable, Type> typeArgs) {
            TypeVariable typeParam = Map.class.getTypeParameters()[1];
            Type valueType = SubstituteTypeVariable.$(typeParam, typeArgs);
            Decoder valueDecoder = spi.decoderOf(valueType);
            return valueDecoder;
        }

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
                Object key = keyDecoder.decode(source);
                if (source.read() != ':') {
                    throw source.reportError("expect :");
                }
                if (source.decodeNull()) {
                    map.put(key, null);
                } else {
                    Object value = valueDecoder.decode(source);
                    map.put(key, value);
                }
            } while ((b = source.read()) == ',');
            if (b != '}') {
                throw source.reportError("expect }");
            }
        }
    }
}
