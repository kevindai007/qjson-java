package org.qjson.codegen;

import org.qjson.TypeLiteral;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SubstituteTypeVariableTest {

    public static class MyClass1<E extends Date> {
    }

    @Test
    public void use_lower_bound_if_not_specified() {
        TypeVariable param = MyClass1.class.getTypeParameters()[0];
        Type sub = SubstituteTypeVariable.$(param, Collections.emptyMap());
        Assert.assertEquals(Date.class, sub);
    }

    public static class MyClass2<E> {
    }

    @Test
    public void no_bound_use_object() {
        TypeVariable param = MyClass2.class.getTypeParameters()[0];
        Type sub = SubstituteTypeVariable.$(param, Collections.emptyMap());
        Assert.assertEquals(Object.class, sub);
    }

    @Test
    public void wildcard_use_object() {
        TypeLiteral typeLiteral = new TypeLiteral<MyClass2<?>>(){};
        Map<TypeVariable, Type> typeArgs = new HashMap<>();
        CollectTypeVariables.$(typeLiteral.$(), typeArgs);
        TypeVariable param = MyClass2.class.getTypeParameters()[0];
        Type sub = SubstituteTypeVariable.$(param, typeArgs);
        Assert.assertEquals(Object.class, sub);
    }

    @Test
    public void wildcard_extends() {
        TypeLiteral typeLiteral = new TypeLiteral<MyClass2<? extends Date>>(){};
        Map<TypeVariable, Type> typeArgs = new HashMap<>();
        CollectTypeVariables.$(typeLiteral.$(), typeArgs);
        TypeVariable param = MyClass2.class.getTypeParameters()[0];
        Type sub = SubstituteTypeVariable.$(param, typeArgs);
        Assert.assertEquals(Date.class, sub);
    }

    @Test
    public void wildcard_super() {
        TypeLiteral typeLiteral = new TypeLiteral<MyClass2<? super Date>>(){};
        Map<TypeVariable, Type> typeArgs = new HashMap<>();
        CollectTypeVariables.$(typeLiteral.$(), typeArgs);
        TypeVariable param = MyClass2.class.getTypeParameters()[0];
        Type sub = SubstituteTypeVariable.$(param, typeArgs);
        Assert.assertEquals(Date.class, sub);
    }
}