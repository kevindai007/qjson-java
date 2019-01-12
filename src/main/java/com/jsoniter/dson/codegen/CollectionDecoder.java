package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Line;

interface CollectionDecoder {
    static void $(Gen g, Class clazz) {
        g.__("java.util.Collection col = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        g.__(new Line("return col;"));
    }
}
