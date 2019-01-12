package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;

interface ArrayDecoder {

    static void $(Gen g, Class clazz) {
        g.__(new Line("byte b = source.peek();"));
        g.__(new Line("if (b != '[') { throw source.reportError(\"expect [\"); }"));
        g.__(new Line("source.next();"));
        g.__(new Line("b = source.peek();"));
        g.__("if (b == ']') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__("return new "
            ).__(clazz.getComponentType().getCanonicalName()
            ).__("[0];");
        })).__(new Line("}"));
        g.__(clazz.getCanonicalName()
        ).__(" arr = new "
        ).__(clazz.getComponentType().getCanonicalName()
        ).__(new Line("[0];"));
        g.__(new Line("return arr;"));
    }
}
