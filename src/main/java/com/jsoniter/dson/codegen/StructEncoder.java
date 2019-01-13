package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Line;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface StructEncoder {

    static void $(Gen g, Class clazz) {
        // {
        g.__(new Line("sink.write('{');"));
        // cast to struct
        g.__(clazz.getCanonicalName()
        ).__(" obj = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")val;"));
        // foreach fields
        for (Field field : clazz.getFields()) {
            g.__("sink.encodeString(\""
            ).__(field.getName()
            ).__(new Line("\");"));
            g.__(new Line("sink.write(':');"));
            g.__("sink.encodeObject(obj."
            ).__(field.getName()
            ).__(new Line(");"));
        }
        // foreach methods
        for (Method method : clazz.getMethods()) {
            String propName = getterPropName(method);
            if (propName == null) {
                continue;
            }
            g.__("sink.encodeString(\""
            ).__(propName
            ).__(new Line("\");"));
            g.__(new Line("sink.write(':');"));
            g.__("sink.encodeObject(obj."
            ).__(method.getName()
            ).__(new Line("());"));
        }
        // }
        g.__(new Line("sink.write('}');"));
    }

    static String getterPropName(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            return null;
        }
        if (Object.class.equals(method.getDeclaringClass())) {
            return null;
        }
        String methodName = method.getName();
        if (method.getReturnType().equals(Void.TYPE)) {
            return null;
        }
        if (method.getParameterCount() != 0) {
            return null;
        }
        if (methodName.startsWith("get")) {
            return decapitalize(methodName.substring(3));
        }
        try {
            Field field = method.getDeclaringClass().getDeclaredField(methodName);
            if (field.getType().equals(method.getReturnType())) {
                return methodName;
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
        return null;
    }

    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * @param name The string to be decapitalized.
     * @return The decapitalized version of the string.
     */
    static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
