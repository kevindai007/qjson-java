package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.DsonSpi;
import com.jsoniter.dson.spi.StructDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

public class StructEncoderGenerator implements Generator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        List<StructDescriptor.Prop> props = getProperties(cfg, spi, clazz);
        return new HashMap<String, Object>(){{
            put("props", props);
        }};
    }

    @Override
    public void genFields(Map<String, Object> args) {

    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {

    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        // {
        g.__(new Line("sink.write('{');"));
        // cast to struct
        g.__(clazz.getCanonicalName()
        ).__(" obj = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")val;"));
        // foreach properties
        for (StructDescriptor.Prop prop : props) {
            g.__("sink.encodeString("
            ).__(asStringLiteral(prop.name)
            ).__(new Line(");"));
            g.__(new Line("sink.write(':');"));
            if (prop.field != null) {
                g.__("sink.encodeObject(obj."
                ).__(prop.field.getName()
                ).__(new Line(");"));
            } else {
                g.__("sink.encodeObject(obj."
                ).__(prop.method.getName()
                ).__(new Line("());"));
            }
        }
        // }
        g.__(new Line("sink.write('}');"));
    }

    private static String asStringLiteral(String str) {
        return "\"" + str.replace("\\", "\\\\")
                .replace("\"", "\\\"") + "\"";
    }

    static List<StructDescriptor.Prop> getProperties(Codegen.Config cfg, DsonSpi spi, Class clazz) {
        StructDescriptor struct = StructDescriptor.create(clazz, spi, cfg.customizeStruct);
        Map<String, StructDescriptor.Prop> props = new HashMap<>();
        for (List<StructDescriptor.Prop> methods : struct.methods.values()) {
            for (StructDescriptor.Prop method : methods) {
                String propName = getterPropName(method.method);
                if (propName == null) {
                    continue;
                }
                if (method.name.isEmpty()) {
                    method.name = propName;
                }
                props.put(method.name, method);
            }
        }
        for (StructDescriptor.Prop field : struct.fields.values()) {
            if (field.name.isEmpty()) {
                field.name = field.field.getName();
            }
            props.put(field.name, field);
        }
        Function<List<StructDescriptor.Prop>, List<StructDescriptor.Prop>> sortProperties = struct.sortProperties;
        if (sortProperties == null) {
            sortProperties = properties -> {
                Collections.sort(properties, Comparator.comparing(o -> o.name));
                return properties;
            };
        }
        return sortProperties.apply(new ArrayList<>(props.values()));
    }

    private static String getterPropName(Method method) {
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
    private static String decapitalize(String name) {
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
