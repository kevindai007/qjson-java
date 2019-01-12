package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;

interface ArrayEncoder {

    static String $(Class clazz) {
        Gen g = new Gen();
        g.__(new Line("package gen;"));
        g.__("public class GeneratedEncoder implements "
        ).__(Encoder.class.getCanonicalName()
        ).__(" {"
        ).__(new Indent(() -> classBody(g, clazz))
        ).__(new Line("}"));
        return g.toString();
    }

    static void classBody(Gen g, Class clazz) {
        g.__("public void encode("
        ).__(EncoderSink.class.getCanonicalName()
        ).__(" sink, Object val) {"
        ).__(new Indent(() -> methodBody(g, clazz))
        ).__(new Line("}"));
    }

    static void methodBody(Gen g, Class clazz) {
        g.__(clazz.getCanonicalName()
        ).__(" arr = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")val;"));
        g.__(new Line("sink.write('[');"));
        g.__("for (int i = 0; i < arr.length; i++) {"
        ).__(new Indent(() -> {
            g.__(new Line("if (i > 0) { sink.write(','); }"));
            g.__(new Line("sink.encodeObject(arr[i]);"));
        })
        ).__(new Line("}"));
        g.__(new Line("sink.write(']');"));
    }
}
