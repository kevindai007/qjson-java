package com.jsoniter.dson.spi;

import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class StructDescriptor {

    private final Class clazz;
    public final Map<String, Prop> fields = new HashMap<>();
    public final Map<String, Prop> methods = new HashMap<>();

    public StructDescriptor(Class clazz) {
        this.clazz = clazz;
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Object.class.equals(field.getDeclaringClass())) {
                continue;
            }
            Prop member = new Prop(field);
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
            Prop member = new Prop(method);
            if (member.getAnnotation(Transient.class) != null && member.getAnnotation(DsonProperty.class) == null) {
                Transient transientAnnotation = member.getAnnotation(Transient.class);
                if (transientAnnotation.value()) {
                    member.setAnnotation(DsonProperty.class, new DsonProperty.Ignore());
                }
            }
            methods.put(method.getName(), member);
        }
    }

    public static class Prop {

        @DsonProperty("123")
        public final Field field;
        public final Method method;
        public Encoder encoder;
        public Decoder decoder;
        private final Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
        // shouldEncode can be used to omit empty value when encoding
        public Predicate<Object> shouldEncode;

        public Prop(Field field) {
            this.field = field;
            this.method = null;
        }

        public Prop(Method method) {
            this.method = method;
            this.field = null;
        }

        public <T extends Annotation> T getAnnotation(Class<T> clazz) {
            Object annotation = annotations.get(clazz);
            if (annotation != null) {
                return (T) annotation;
            }
            if (field != null) {
                return field.getAnnotation(clazz);
            }
            return method.getAnnotation(clazz);
        }

        public <T extends Annotation, A extends T> void setAnnotation(Class<T> clazz, A annotation) {
            annotations.put(clazz, annotation);
        }
    }
}
