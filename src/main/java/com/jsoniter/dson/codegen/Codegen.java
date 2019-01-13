package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.decode.DsonDecodeException;
import com.jsoniter.dson.encode.DsonEncodeException;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Codegen {

    private final Config cfg;
    private final Function<Type, Decoder> decoderProvider;
    private int counter;

    public static class Config {
        public InMemoryJavaCompiler compiler;
        public Function<Class, Class> chooseImpl;
    }

    public Codegen(Config cfg, Function<Type, Decoder> decoderProvider) {
        this.cfg = cfg;
        this.decoderProvider = decoderProvider;
    }

    public synchronized Decoder generateDecoder(Type type) {
        Map<TypeVariable, Type> typeArgs = new HashMap<>();
        Class clazz = CollectTypeVariables.$(type, typeArgs);
        if (clazz == null) {
            throw new DsonDecodeException("can not cast to class: " + type);
        }
        return generateDecoder(clazz, typeArgs);
    }

    public synchronized Decoder generateDecoder(Class clazz, Map<TypeVariable, Type> typeArgs) {
        String decoderClassName = "GeneratedDecoder" + (counter++);
        Gen g = new Gen();
        g.__(new Line("package gen;"));
        g.__("public class "
        ).__(decoderClassName
        ).__(" implements "
        ).__(Decoder.class.getCanonicalName()
        ).__(" {"
        ).__(new Indent(() -> {
            boolean isJavaUtil = isJavaUtil(clazz);
            if (clazz.isArray()) {
                ArrayDecoder.$(g, decoderClassName, clazz);
            } else if (Collection.class.isAssignableFrom(clazz) && isJavaUtil) {
                CollectionDecoder.$(g, decoderClassName, clazz);
            } else if (Map.class.isAssignableFrom(clazz) && isJavaUtil) {
                MapDecoder.$(g, decoderClassName, clazz);
            } else {
                StructDecoder.$(g, decoderClassName, clazz);
            }
        })).__(new Line("}"));
        String src = g.toString();
        try {
            printSourceCode(clazz, src);
            Class<?> decoderClass = cfg.compiler.compile("gen." + decoderClassName, src);
            Constructor<?> ctor = decoderClass.getConstructor(Codegen.Config.class, Function.class, Class.class, Map.class);
            return (Decoder) ctor.newInstance(cfg, decoderProvider, clazz, typeArgs);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DsonDecodeException(e);
        }
    }

    public synchronized Encoder generateEncoder(Class clazz) {
        String encoderClassName = "GeneratedEncoder" + (counter++);
        Gen g = new Gen();
        g.__(new Line("package gen;"));
        g.__("public class "
        ).__(encoderClassName
        ).__(" implements "
        ).__(Encoder.class.getCanonicalName()
        ).__(" {"
        ).__(new Indent(() -> {
            g.__("public void encode("
            ).__(EncoderSink.class.getCanonicalName()
            ).__(" sink, Object val) {"
            ).__(new Indent(() -> {
                g.__("try {"
                ).__(new Indent(() -> {

                    if (clazz.isArray()) {
                        ArrayEncoder.$(g, clazz);
                    } else {
                        StructEncoder.$(g, clazz);
                    }
                })).__("} catch (RuntimeException e) {"
                ).__(new Indent(() -> {
                    g.__("throw e;");
                })).__("} catch (Exception e) {"
                ).__(new Indent(() -> {
                    g.__("throw new "
                    ).__(DsonEncodeException.class.getCanonicalName()
                    ).__("(e);");
                })).__(new Line("}"));
            })).__(new Line("}"));
        })).__(new Line("}"));
        String src = g.toString();
        try {
            printSourceCode(clazz, src);
            Class<?> encoderClass = cfg.compiler.compile("gen." + encoderClassName, src);
            return (Encoder) encoderClass.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DsonEncodeException(e);
        }
    }

    private static void printSourceCode(Class clazz, String src) {
        if (!"true".equals(System.getenv("DSON_DEBUG"))) {
            return;
        }
        System.out.println("=== " + clazz.getCanonicalName() + " ===");
        System.out.println(src);
    }

    public static boolean isJavaUtil(Class clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.getName().startsWith("java.util.")) {
            return true;
        }
        return isJavaUtil(clazz.getSuperclass());
    }
}
