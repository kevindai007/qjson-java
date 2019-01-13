package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface CollectionDecoder {

    interface Helper {

        static Decoder getElemDecoder(Function<Type, Decoder> decoderProvider, Map<TypeVariable, Type> typeArgs) {
            TypeVariable typeParam = Collection.class.getTypeParameters()[0];
            Type elemType = SubstituteTypeVariable.$(typeParam, typeArgs);
            Decoder elemDecoder = decoderProvider.apply(elemType);
            return elemDecoder;
        }

        static void fill(DecoderSource source, Decoder elemDecoder, Collection col) {
            byte b;
            do {
                if (source.decodeNull()) {
                    col.add(null);
                } else {
                    col.add(elemDecoder.decode(source));
                }
            } while ((b = source.read()) == ',');
            if (b != ']') {
                throw source.reportError("expect ]");
            }
        }
    }

    static void $(Gen g, String decoderClassName, Class clazz) {
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" elemDecoder;"));
        GenDecoder.ctor(g, decoderClassName, new Indent(() -> {
            g.__("this.elemDecoder = "
            ).__(Helper.class.getCanonicalName()
            ).__(new Line(".getElemDecoder(decoderProvider, typeArgs);"));
        }));
        GenDecoder.method(g, new Indent(() -> {
            g.__(ArrayDecoder.Helper.class.getCanonicalName()).__(new Line(".expectArrayHead(source);"));
            // if collection is []
            g.__("java.util.Collection col = new "
            ).__(clazz.getCanonicalName()
            ).__(new Line("();"));
            g.__("if (source.peek() == ']') { "
            ).__(new Indent(() -> {
                g.__(new Line("source.next();"));
                g.__(new Line("return col;"));
            })).__(new Line("}"));
            // fill collection
            g.__(Helper.class.getCanonicalName()).__(new Line(".fill(source, elemDecoder, col);"));
            g.__(new Line("return col;"));
        }));
    }
}
