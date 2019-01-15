package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.codegen.gen.Indent;
import org.qjson.codegen.gen.Line;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;
import org.qjson.spi.QJsonSpi;
import org.qjson.spi.StructDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

public class StructDecoderGenerator implements DecoderGenerator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, QJsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
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
    public void genDecode(Gen g, Map<String, Object> args, Class clazz) {
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            // do not support constructor binding
            g.__(new Line("return null;"));
            return;
        }
        g.__("return new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
    }

    @Override
    public void genDecodeProperties(Gen g, Map<String, Object> args, Class clazz) {
        List<StructDescriptor.Prop> props = (List<StructDescriptor.Prop>) args.get("props");
        g.__(Helper.class.getCanonicalName()).__(new Line(".expectMapHead(source);"));
        // if object is {}
        g.__(new Line("byte b = source.peek();"));
        g.__("if (b == '}') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__(new Line("return;"));
        })).__(new Line("}"));

        g.__(clazz.getCanonicalName()
        ).__(" struct = ("
        ).__(clazz.getCanonicalName()
        ).__(new Line(")obj;"));
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
    }

    public Indent propCase(Gen g, int i, StructDescriptor.Prop prop) {
        return new Indent(() -> {
            g.__("int oldPath = source.currentPath().enterStructField("
            ).__(StructEncoderGenerator.asStringLiteral(prop.name)
            ).__(new Line(");"));
            if (prop.field != null) {
                setPropertyByField(g, i, prop);
            } else {
                setPropertyBySetter(g, i, prop);
            }
            g.__(new Line("source.currentPath().exit(oldPath);"));
            g.__(new Line("break;"));
        });
    }

    private static void setPropertyBySetter(Gen g, int i, StructDescriptor.Prop prop) {
        g.__("struct."
        ).__(prop.method.getName()
        ).__("(("
        ).__(prop.method.getParameterTypes()[0].getCanonicalName()
        ).__(")source.decodeObject(decoder"
        ).__(i
        ).__(new Line("));"));
    }

    private static void setPropertyByField(Gen g, int i, StructDescriptor.Prop prop) {
        g.__("struct."
        ).__(prop.field.getName()
        ).__(" = ("
        ).__(prop.field.getType().getCanonicalName()
        ).__(")source.decodeObject(decoder"
        ).__(i
        ).__(new Line(");"));
    }

    static List<StructDescriptor.Prop> getProperties(Codegen.Config cfg, QJsonSpi spi, Class clazz) {
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

    public interface Helper {

        static void expectMapHead(DecoderSource source) {
            byte b = source.peek();
            if (b != '{') {
                throw source.reportError("expect {");
            }
            source.next();
        }
    }
}
