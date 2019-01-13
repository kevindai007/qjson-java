package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.spi.DsonSpi;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public interface Generator {

    Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs);

    void genFields(Gen g, Map<String, Object> args);

    void genCtor(Gen g, Map<String, Object> args);

    void genMethod(Gen g, Map<String, Object> args, Class clazz);
}
