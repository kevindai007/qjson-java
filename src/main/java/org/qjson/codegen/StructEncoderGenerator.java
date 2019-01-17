package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.codegen.gen.Line;
import org.qjson.spi.Encoder;
import org.qjson.spi.QJsonSpi;
import org.qjson.spi.StructDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class StructEncoderGenerator implements EncoderGenerator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, QJsonSpi spi, Class clazz) {
        List<StructDescriptor.Prop> props = getProperties(cfg, spi, clazz);
        return new HashMap<String, Object>() {{
            put("props", props);
            put("spi", spi);
        }};
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        for (int i = 0; i < props.size(); i++) {
            g.__("private final "
            ).__(Encoder.class.getCanonicalName()
            ).__(" encoder"
            ).__(i
            ).__(new Line(";"));
            g.__("private final "
            ).__(Predicate.class.getCanonicalName()
            ).__(" shouldEncode"
            ).__(i
            ).__(new Line(";"));
        }
        g.__("private final "
        ).__(Encoder.Provider.class.getCanonicalName()
        ).__(new Line(" spi;"));
    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {
        g.__("java.util.List<"
        ).__(StructDescriptor.Prop.class.getCanonicalName()
        ).__("> props = (java.util.List<"
        ).__(StructDescriptor.Prop.class.getCanonicalName()
        ).__(new Line(">)args.get(\"props\");"));
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        for (int i = 0; i < props.size(); i++) {
            g.__("this.encoder").__(i).__(" = props.get(").__(i).__(new Line(").encoder;"));
            g.__("this.shouldEncode").__(i).__(" = props.get(").__(i).__(new Line(").shouldEncode;"));
        }
        g.__("this.spi = ("
        ).__(Encoder.Provider.class.getCanonicalName()
        ).__(new Line(")args.get(\"spi\");"));
    }

    @Override
    public void genEncode(Gen g, Map<String, Object> args, Class clazz) {
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        // {
        g.__(new Line("sink.write('{');"));
        // cast to struct
        g.__(clazz.getCanonicalName()
        ).__(" obj = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")val;"));
        // foreach properties
        g.__(new Line("int oldPath;"));
        g.__(new Line("boolean isFirst = true;"));
        for (int i = 0; i < props.size(); i++) {
            StructDescriptor.Prop prop = props.get(i);
            String expr;
            if (prop.field != null) {
                expr = "obj." + prop.field.getName();
            } else {
                expr = "obj." + prop.method.getName() + "()";
            }
            if (prop.shouldEncode != null) {
                g.__("if (this.shouldEncode").__(i).__(".test(").__(expr).__(new Line(")) {"));
            }
            g.__(new Line("if (isFirst) { isFirst = false; } else { sink.write(','); }"));
            g.__("sink.encodeString("
            ).__(asStringLiteral(prop.name)
            ).__(new Line(");"));
            g.__(new Line("sink.write(':');"));
            g.__("oldPath = sink.currentPath().enterStructField("
            ).__(asStringLiteral(prop.name)
            ).__(new Line(");"));
            if (prop.encoder == null) {
                g.__("sink.encodeObject(").__(expr).__(new Line(", spi);"));
            } else {
                g.__("sink.encodeObject(").__(expr).__(", this.encoder").__(i).__(new Line(");"));
            }
            g.__(new Line("sink.currentPath().exit(oldPath);"));
            if (prop.shouldEncode != null) {
                g.__(new Line("}"));
            }
        }
        // }
        g.__(new Line("sink.write('}');"));
    }

    static String asStringLiteral(String str) {
        return "\"" + str.replace("\\", "\\\\")
                .replace("\"", "\\\"") + "\"";
    }

    static List<StructDescriptor.Prop> getProperties(Codegen.Config cfg, QJsonSpi spi, Class clazz) {
        StructDescriptor struct = StructDescriptor.create(clazz, spi, cfg.customizeStruct);
        Map<String, StructDescriptor.Prop> props = new HashMap<>();
        for (StructDescriptor.Prop field : struct.fields.values()) {
            if (field.name.isEmpty()) {
                field.name = field.field.getName();
            }
            props.put(field.name, field);
        }
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
