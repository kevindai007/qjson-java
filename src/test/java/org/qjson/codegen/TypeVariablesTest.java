package org.qjson.codegen;

import org.junit.Assert;
import org.junit.Test;
import org.qjson.spi.TypeVariables;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;

public class TypeVariablesTest {

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
        TypeVariables.collect(DerivedClass.class, typeArgs);
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
        TypeVariables.collect(FinalClass.class, typeArgs);
        TypeVariable typeParam = BaseClass.class.getTypeParameters()[0];
        Assert.assertEquals(String.class, typeArgs.get(typeParam));
    }
}
