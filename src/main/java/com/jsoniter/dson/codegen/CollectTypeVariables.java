package com.jsoniter.dson.codegen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

interface CollectTypeVariables {
    static Class $(Type type, Map<TypeVariable, Type> collector) {
        if (Object.class.equals(type)) {
            return null;
        }
        if (type instanceof Class) {
            Class clazz = (Class) type;
            for (Type inf : clazz.getGenericInterfaces()) {
                CollectTypeVariables.$(inf, collector);
            }
            CollectTypeVariables.$(clazz.getGenericSuperclass(), collector);
            return clazz;
        }
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] theTypeArgs = parameterizedType.getActualTypeArguments();
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            return null;
        }
        Class clazz = (Class) rawType;
        TypeVariable[] theTypeParams = clazz.getTypeParameters();
        if (theTypeArgs.length == theTypeParams.length) {
            for (int i = 0; i < theTypeParams.length; i++) {
                TypeVariable theTypeParam = theTypeParams[i];
                Type theTypeArg = theTypeArgs[i];
                // if the type arg is also a type param, try substitute
                collector.put(theTypeParam, collector.getOrDefault(theTypeArg, theTypeArg));
            }
        }
        for (Type inf : clazz.getGenericInterfaces()) {
            CollectTypeVariables.$(inf, collector);
        }
        CollectTypeVariables.$(clazz.getGenericSuperclass(), collector);
        return clazz;
    }
}
