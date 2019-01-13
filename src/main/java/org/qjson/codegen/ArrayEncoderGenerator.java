package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.codegen.gen.Indent;
import org.qjson.codegen.gen.Line;
import org.qjson.spi.DsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

class ArrayEncoderGenerator implements Generator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        return null;
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {

    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {

    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
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
