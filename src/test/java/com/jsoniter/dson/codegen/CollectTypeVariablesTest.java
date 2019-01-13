package com.jsoniter.dson.codegen;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;

public class CollectTypeVariablesTest {

    private static class BaseClass<T> {
        public T field1;
    }

    private interface BaseInf<T> {

    }

    private static class DerivedClass extends BaseClass<String> implements BaseInf<String> {

    }

    @Test
    public void one_layer() {
        HashMap<TypeVariable, Type> typeArgs = new HashMap<>();
        CollectTypeVariables.$(DerivedClass.class, typeArgs);
        TypeVariable typeParam = BaseClass.class.getTypeParameters()[0];
        Assert.assertEquals(String.class, typeArgs.get(typeParam));
        typeParam = BaseInf.class.getTypeParameters()[0];
        Assert.assertEquals(String.class, typeArgs.get(typeParam));
    }

    private static class GenericDerivedClass<T> extends BaseClass<T> {
    }

    private static class FinalClass extends GenericDerivedClass<String> {
    }

    @Test
    public void two_layers() {
        HashMap<TypeVariable, Type> typeArgs = new HashMap<>();
        CollectTypeVariables.$(FinalClass.class, typeArgs);
        TypeVariable typeParam = BaseClass.class.getTypeParameters()[0];
        Assert.assertEquals(String.class, typeArgs.get(typeParam));
    }
}
