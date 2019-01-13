package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.encode.DsonEncodeException;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DsonSpi;
import com.jsoniter.dson.spi.EncoderSink;

public class GenEncoder {

    static void ctor(Gen g, String encoderClassName, Indent indent) {
        g.__("public "
        ).__(encoderClassName
        ).__("("
        ).__(Codegen.Config.class.getCanonicalName()
        ).__(" cfg, "
        ).__(DsonSpi.class.getCanonicalName()
        ).__(" spi, Class clazz, java.util.Map<java.lang.reflect.TypeVariable, java.lang.reflect.Type> typeArgs) {"
        ).__(indent).__(new Line("}"));
    }

    static void method(Gen g, Indent indent) {
        g.__("public void encode("
        ).__(EncoderSink.class.getCanonicalName()
        ).__(" sink, Object val) {"
        ).__(new Indent(() -> {
            g.__("try {"
            ).__(indent
            ).__("} catch (RuntimeException e) {"
            ).__(new Indent(() -> {
                g.__("throw e;");
            })).__("} catch (Exception e) {"
            ).__(new Indent(() -> {
                g.__("throw new "
                ).__(DsonEncodeException.class.getCanonicalName()
                ).__("(e);");
            })).__(new Line("}"));
        }));
    }
}
