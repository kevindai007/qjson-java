package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;

import java.lang.reflect.Field;

public interface StructDecoder {

    static void $(Gen g, String decoderClassName, Class clazz) {
        for (Field field : clazz.getFields()) {
            String fieldDecoderName = field.getName() + "Decoder";
            g.__("private final "
            ).__(Decoder.class.getCanonicalName()
            ).__(' '
            ).__(fieldDecoderName
            ).__(new Line(";"));
        }
        GenDecoder.ctor(g, decoderClassName, new Indent(() -> {
        }));
        GenDecoder.method(g, new Indent(() -> {
            try {
                clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                // do not support constructor binding
                g.__(new Line("return null;"));
                return;
            }
            g.__(MapDecoder.Helper.class.getCanonicalName()).__(new Line(".expectMapHead(source);"));
            g.__(clazz.getCanonicalName()
            ).__(" obj = new "
            ).__(clazz.getCanonicalName()
            ).__(new Line("();"));
            // if map is {}
            g.__("if (source.peek() == '}') { "
            ).__(new Indent(() -> {
                g.__(new Line("source.next();"));
                g.__(new Line("return obj;"));
            })).__(new Line("}"));
            // fill map
            g.__(new Line("return obj;"));
        }));
    }
}
