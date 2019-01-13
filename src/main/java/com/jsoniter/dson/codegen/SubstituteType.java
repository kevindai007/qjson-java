package com.jsoniter.dson.codegen;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

interface SubstituteType {
    static Type $(TypeVariable typeParam, Map<TypeVariable, Type> typeArgs) {
        return typeArgs.get(typeParam);
    }
}
