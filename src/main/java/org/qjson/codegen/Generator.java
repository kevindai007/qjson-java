package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.spi.DsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public interface Generator {

    // typeArgs will be null for encoder generator
    Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs);

    void genFields(Gen g, Map<String, Object> args);

    void genCtor(Gen g, Map<String, Object> args);

    void genMethod(Gen g, Map<String, Object> args, Class clazz);
}
