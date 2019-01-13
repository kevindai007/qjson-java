package com.jsoniter.dson.codegen;

import com.jsoniter.dson.spi.DsonProperty;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class StructDescriptor {

    private final Class clazz;
    public final Map<String, StructProperty> fields = new HashMap<>();
    public final Map<String, StructProperty> methods = new HashMap<>();

    public StructDescriptor(Class clazz) {
        this.clazz = clazz;
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Object.class.equals(field.getDeclaringClass())) {
                continue;
            }
            StructProperty member = new StructProperty(field);
            if (Modifier.isTransient(field.getModifiers()) && member.getAnnotation(DsonProperty.class) == null) {
                member.setAnnotation(DsonProperty.class, new DsonProperty.Ignore());
            }
            fields.put(field.getName(), member);
        }
        for (Method method : clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (Object.class.equals(method.getDeclaringClass())) {
                continue;
            }
            StructProperty member = new StructProperty(method);
            if (member.getAnnotation(Transient.class) != null && member.getAnnotation(DsonProperty.class) == null) {
                Transient transientAnnotation = member.getAnnotation(Transient.class);
                if (transientAnnotation.value()) {
                    member.setAnnotation(DsonProperty.class, new DsonProperty.Ignore());
                }
            }
            methods.put(method.getName(), member);
        }
    }
}
