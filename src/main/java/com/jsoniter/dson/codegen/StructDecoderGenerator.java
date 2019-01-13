package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DsonSpi;
import com.jsoniter.dson.spi.StructDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

public class StructDecoderGenerator implements Generator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, DsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        List<StructDescriptor.Prop> props = getProperties(cfg, spi, clazz);
        return new HashMap<String, Object>() {{
            put("props", props);
        }};
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        for (int i = 0; i < props.size(); i++) {
            g.__("private final "
            ).__(Decoder.class.getCanonicalName()
            ).__(" decoder"
            ).__(i
            ).__(new Line(";"));
        }
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
            g.__("this.decoder").__(i).__(" = props.get(").__(i).__(new Line(").decoder;"));
        }
    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            // do not support constructor binding
            g.__(new Line("return null;"));
            return;
        }
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        g.__(MapDecoderGenerator.Helper.class.getCanonicalName()).__(new Line(".expectMapHead(source);"));
        g.__(clazz.getCanonicalName()
        ).__(" obj = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        // if object is {}
        g.__(new Line("byte b = source.peek();"));
        g.__("if (b == '}') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__(new Line("return obj;"));
        })).__(new Line("}"));
        g.__("do {"
        ).__(new Indent(() -> {
            g.__(new Line("String field = source.decodeString();"));
            g.__(new Line("if (source.read() != ':') { throw source.reportError(\"expect :\"); }"));
            g.__("switch (field) {"
            ).__(new Indent(() -> {
                for (int i = 0; i < props.size(); i++) {
                    StructDescriptor.Prop prop = props.get(i);
                    g.__("case "
                    ).__(StructEncoderGenerator.asStringLiteral(prop.name)
                    ).__(": "
                    ).__(propCase(g, i, prop));
                }
                g.__(new Line("default: source.skip(); "));
            })).__(new Line("}"));
        })).__(new Line("} while ((b = source.read()) == ',');"));
        g.__(new Line("if (b != '}') { throw source.reportError(\"expect }\"); }"));
        // fill object
        g.__(new Line("return obj;"));
    }

    public Indent propCase(Gen g, int i, StructDescriptor.Prop prop) {
        return new Indent(() -> {
            if (prop.field != null) {
                setPropertyByField(g, i, prop);
            } else {
                setPropertyBySetter(g, i, prop);
            }
            g.__(new Line("break;"));
        });
    }

    private static void setPropertyBySetter(Gen g, int i, StructDescriptor.Prop prop) {
        g.__("obj."
        ).__(prop.method.getName()
        ).__("(("
        ).__(prop.method.getParameterTypes()[0].getCanonicalName()
        ).__(")decoder"
        ).__(i
        ).__(new Line(".decode(source));"));
    }

    private static void setPropertyByField(Gen g, int i, StructDescriptor.Prop prop) {
        g.__("obj."
        ).__(prop.field.getName()
        ).__(" = ("
        ).__(prop.field.getType().getCanonicalName()
        ).__(")decoder"
        ).__(i
        ).__(new Line(".decode(source);"));
    }

    static List<StructDescriptor.Prop> getProperties(Codegen.Config cfg, DsonSpi spi, Class clazz) {
        StructDescriptor struct = StructDescriptor.create(clazz, spi, cfg.customizeStruct);
        Map<String, StructDescriptor.Prop> props = new HashMap<>();
        for (List<StructDescriptor.Prop> methods : struct.methods.values()) {
            for (StructDescriptor.Prop method : methods) {
                String propName = setterPropName(method.method);
                if (propName == null) {
                    continue;
                }
                if (method.name.isEmpty()) {
                    method.name = propName;
                }
                if (method.decoder == null) {
                    method.decoder = spi.decoderOf(method.method.getGenericParameterTypes()[0]);
                }
                props.put(method.name, method);
            }
        }
        for (StructDescriptor.Prop field : struct.fields.values()) {
            if (field.name.isEmpty()) {
                field.name = field.field.getName();
            }
            if (field.decoder == null) {
                field.decoder = spi.decoderOf(field.field.getGenericType());
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

    private static String setterPropName(Method method) {
        String methodName = method.getName();
        if (method.getParameterCount() != 1) {
            return null;
        }
        if (methodName.startsWith("set")) {
            return StructEncoderGenerator.decapitalize(methodName.substring(3));
        }
        try {
            Field field = method.getDeclaringClass().getDeclaredField(methodName);
            if (field.getType().equals(method.getParameterTypes()[0])) {
                return methodName;
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
        return null;
    }
}
