package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.function.Function;

public interface MapDecoder {

    interface Helper {

        static Decoder getKeyDecoder(Function<Type, Decoder> decoderProvider, Map<TypeVariable, Type> typeArgs) {
            TypeVariable typeParam = Map.class.getTypeParameters()[0];
            Type keyType = SubstituteType.$(typeParam, typeArgs);
            Decoder keyDecoder = decoderProvider.apply(keyType);
            return keyDecoder;
        }

        static Decoder getValueDecoder(Function<Type, Decoder> decoderProvider, Map<TypeVariable, Type> typeArgs) {
            TypeVariable typeParam = Map.class.getTypeParameters()[1];
            Type valueType = SubstituteType.$(typeParam, typeArgs);
            Decoder valueDecoder = decoderProvider.apply(valueType);
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
                Object value = valueDecoder.decode(source);
                map.put(key, value);
            } while ((b = source.read()) == ',');
            if (b != '}') {
                throw source.reportError("expect }");
            }
        }
    }

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
            ).__(new Line(".getKeyDecoder(decoderProvider, typeArgs);"));
            g.__("this.valueDecoder = "
            ).__(Helper.class.getCanonicalName()
            ).__(new Line(".getValueDecoder(decoderProvider, typeArgs);"));
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
}
