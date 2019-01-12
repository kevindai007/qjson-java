package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.decode.DsonDecodeException;
import com.jsoniter.dson.encode.DsonEncodeException;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;
import com.jsoniter.dson.spi.Encoder;
import com.jsoniter.dson.spi.EncoderSink;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.util.Collection;
import java.util.Map;

public class Codegen {

    private final InMemoryJavaCompiler compiler;
    private int counter;

    public Codegen(InMemoryJavaCompiler compiler) {
        this.compiler = compiler;
    }

    public Codegen() {
        this(InMemoryJavaCompiler.newInstance()
                .ignoreWarnings());
    }

    public synchronized Decoder generateDecoder(Class clazz) {
        String className = "GeneratedDecoder" + (counter++);
        Gen g = new Gen();
        g.__(new Line("package gen;"));
        g.__("public class "
        ).__(className
        ).__(" implements "
        ).__(Decoder.class.getCanonicalName()
        ).__(" {"
        ).__(new Indent(() -> {
            g.__("public Object decode("
            ).__(DecoderSource.class.getCanonicalName()
            ).__(" source) {"
            ).__(new Indent(() -> {
                if (clazz.isArray()) {
                    ArrayDecoder.$(g, clazz);
                } else if (Collection.class.isAssignableFrom(clazz)) {
                    CollectionDecoder.$(g, clazz);
                } else if (Map.class.isAssignableFrom(clazz)) {
                    MapDecoder.$(g, clazz);
                } else {
                    throw new UnsupportedOperationException("not implemented: " + clazz);
                }
            })).__(new Line("}"));
        })).__(new Line("}"));
        String src = g.toString();
        try {
            if ("true".equals(System.getenv("DSON_DEBUG"))) {
                System.out.println(src);
            }
            Class<?> decoderClass = compiler.compile("gen." + className, src);
            return (Decoder) decoderClass.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DsonDecodeException(e);
        }
    }

    public synchronized Encoder generateEncoder(Class clazz) {
        String className = "GeneratedEncoder" + (counter++);
        Gen g = new Gen();
        g.__(new Line("package gen;"));
        g.__("public class "
        ).__(className
        ).__(" implements "
        ).__(Encoder.class.getCanonicalName()
        ).__(" {"
        ).__(new Indent(() -> {
            g.__("public void encode("
            ).__(EncoderSink.class.getCanonicalName()
            ).__(" sink, Object val) {"
            ).__(new Indent(() -> {
                if (clazz.isArray()) {
                    ArrayEncoder.$(g, clazz);
                } else {
                    throw new UnsupportedOperationException("not implemented: " + clazz);
                }
            })).__(new Line("}"));
        })).__(new Line("}"));
        String src = g.toString();
        try {
            if ("true".equals(System.getenv("DSON_DEBUG"))) {
                System.out.println(src);
            }
            Class<?> encoderClass = compiler.compile("gen." + className, src);
            return (Encoder) encoderClass.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DsonEncodeException(e);
        }
    }
}
