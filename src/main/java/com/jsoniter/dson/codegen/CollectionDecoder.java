package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;

interface CollectionDecoder {
    static void $(Gen g, Class clazz) {
        g.__("java.util.Collection col = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        g.__(new Line("byte b = source.peek();"));
        g.__(new Line("if (b != '[') { throw source.reportError(\"expect [\"); }"));
        g.__(new Line("source.next();"));
        g.__(new Line("b = source.peek();"));
        g.__("if (b == ']') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__(new Line("return col;"));
        })).__(new Line("}"));
        g.__(new Line("return col;"));
    }
}
