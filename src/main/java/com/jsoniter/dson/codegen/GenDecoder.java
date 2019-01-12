package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

interface GenDecoder {

    static void ctor(Gen g, String decoderClassName, Indent indent) {
        g.__("public "
        ).__(decoderClassName
        ).__("("
        ).__(Codegen.Config.class.getCanonicalName()
        ).__(" cfg, java.util.function.Function<java.lang.reflect.Type, "
        ).__(Decoder.class.getCanonicalName()
        ).__("> decoderProvider, Class clazz, java.util.Map<java.lang.reflect.TypeVariable, java.lang.reflect.Type> typeArgs) {"
        ).__(indent).__(new Line("}"));
    }

    static void method(Gen g, Indent indent) {
        g.__("public Object decode("
        ).__(DecoderSource.class.getCanonicalName()
        ).__(" source) {"
        ).__(indent).__(new Line("}"));
    }
}
