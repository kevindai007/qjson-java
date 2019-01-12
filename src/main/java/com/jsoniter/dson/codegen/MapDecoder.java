package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Line;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

class MapDecoder {
    static void $(Gen g, Class clazz, Map<TypeVariable, Type> typeArgs) {
        g.__("java.util.Map map = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        g.__(new Line("return map;"));
    }
}
