package com.jsoniter.dson.codegen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

interface SubstituteTypeVariable {
    static Type $(TypeVariable typeParam, Map<TypeVariable, Type> typeArgs) {
        Type sub = typeArgs.get(typeParam);
        if (sub instanceof Class || sub instanceof ParameterizedType) {
            return sub;
        }
        if (sub == null) {
            sub = typeParam;
        }
        if (sub instanceof TypeVariable) {
            return ((TypeVariable) sub).getBounds()[0];
        }
        if (sub instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) sub;
            if (wildcardType.getLowerBounds().length > 0) {
                return wildcardType.getLowerBounds()[0];
            }
            if (wildcardType.getUpperBounds().length > 0) {
                return wildcardType.getUpperBounds()[0];
            }
        }
        return Object.class;
    }
}
