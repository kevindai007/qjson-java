package com.jsoniter.dson.spi;

import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class StructDescriptor {

    private final Class clazz;
    public final Map<String, Prop> fields = new HashMap<>();
    public final Map<String, Prop> methods = new HashMap<>();
    // sort the properties in order to encode
    public Function<List<Prop>, List<Prop>> sortProperties;

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

    public void customize(Consumer<StructDescriptor> customizeStruct) {
        if (customizeStruct != null) {
            customizeStruct.accept(this);
        }
        for (Prop value : fields.values()) {
            DsonProperty dsonProperty = value.getAnnotation(DsonProperty.class);
            copyProp(value, dsonProperty);
        }
    }

    private void copyProp(Prop prop, DsonProperty annotation) {
        if (annotation == null) {
            prop.ignore = false;
            return;
        }
        if (prop.ignore == null) {
            prop.ignore = annotation.ignore();
        }
        if (prop.name == null) {
            prop.name = annotation.value();
        }
        if (prop.encoder == null) {
            prop.encoder = newObject(annotation.encoder());
        }
        if (prop.decoder == null) {
            prop.decoder = newObject(annotation.decoder());
        }
        if (prop.shouldEncode == null) {
            prop.shouldEncode = newObject(annotation.shouldEncode());
        }
    }

    private static <T> T newObject(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isInterface()) {
            return null;
        }
        try {
            return clazz.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("failed to instantiate: " + clazz, e);
        }
    }

    // property can be customized by @DsonProperty
    // or can be modified directly via StructDescriptor
    public static class Prop {

        @DsonProperty("123")
        public final Field field;
        public final Method method;
        private final Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
        public Encoder encoder;
        public Decoder decoder;
        public Predicate<Object> shouldEncode;
        public String name;
        public Boolean ignore;

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
