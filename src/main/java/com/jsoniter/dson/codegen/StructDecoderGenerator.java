package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.DsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class StructDecoderGenerator implements Generator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        return null;
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {
//        for (Field field : clazz.getFields()) {
//            String fieldDecoderName = field.getName() + "Decoder";
//            g.__("private final "
//            ).__(Decoder.class.getCanonicalName()
//            ).__(' '
//            ).__(fieldDecoderName
//            ).__(new Line(";"));
//        }
    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {

    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            // do not support constructor binding
            g.__(new Line("return null;"));
            return;
        }
        g.__(MapDecoderGenerator.Helper.class.getCanonicalName()).__(new Line(".expectMapHead(source);"));
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
    }
}
