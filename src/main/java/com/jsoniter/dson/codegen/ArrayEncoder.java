package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;

interface ArrayEncoder {

    static void $(Gen g, Class clazz) {
        // [
        g.__(new Line("sink.write('[');"));
        // cast to array
        g.__(clazz.getCanonicalName()
        ).__(" arr = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")val;"));
        // foreach
        g.__("for (int i = 0; i < arr.length; i++) {"
        ).__(new Indent(() -> {
                    g.__(new Line("if (i > 0) { sink.write(','); }"));
                    g.__(new Line("sink.encodeObject(arr[i]);"));
                })
        ).__(new Line("}"));
        // ]
        g.__(new Line("sink.write(']');"));
    }
}
