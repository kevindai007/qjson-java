package com.jsoniter.dson.codegen;

import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DsonProperty;
import com.jsoniter.dson.spi.Encoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class StructProperty {

    @DsonProperty("123")
    public final Field field;
    public final Method method;
    public Encoder encoder;
    public Decoder decoder;
    private final Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
    // shouldEncode can be used to omit empty value when encoding
    public Predicate<Object> shouldEncode;

    public StructProperty(Field field) {
        this.field = field;
        this.method = null;
    }

    public StructProperty(Method method) {
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
