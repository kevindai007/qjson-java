package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.decode.DsonDecodeException;
import com.jsoniter.dson.encode.DsonEncodeException;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;
import com.jsoniter.dson.spi.DsonSpi;

interface GenDecoder {

    static void ctor(Gen g, String decoderClassName, Indent indent) {
        g.__("public "
        ).__(decoderClassName
        ).__("("
        ).__(Codegen.Config.class.getCanonicalName()
        ).__(" cfg, "
        ).__(DsonSpi.class.getCanonicalName()
        ).__(" spi, Class clazz, java.util.Map<java.lang.reflect.TypeVariable, java.lang.reflect.Type> typeArgs) {"
        ).__(indent).__(new Line("}"));
    }

    static void method(Gen g, Indent indent) {
        g.__("public Object decode("
        ).__(DecoderSource.class.getCanonicalName()
        ).__(" source) {"
        ).__(new Indent(() -> {
            g.__("try {"
            ).__(indent
            ).__("} catch (RuntimeException e) {"
            ).__(new Indent(() -> {
                g.__("throw e;");
            })).__("} catch (Exception e) {"
            ).__(new Indent(() -> {
                g.__("throw new "
                ).__(DsonDecodeException.class.getCanonicalName()
                ).__("(e);");
            })).__(new Line("}"));
        })).__(new Line("}"));
    }
}
