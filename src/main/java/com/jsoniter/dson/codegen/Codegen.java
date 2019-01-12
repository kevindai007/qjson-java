package com.jsoniter.dson.codegen;

import com.jsoniter.dson.encode.DsonEncodeException;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.Encoder;
import org.mdkt.compiler.InMemoryJavaCompiler;

public class Codegen {

    private final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance()
            .ignoreWarnings();

    public void useOptions(String ...options) {
        compiler.useOptions(options);
    }

    public Decoder generateDecoder(Class clazz) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Encoder generateEncoder(Class clazz) {
        if (clazz.isArray()) {
            String src = ArrayEncoder.$(clazz);
            try {
                if ("true".equals(System.getProperty("DSON_DEBUG"))) {
                    System.out.println(src);
                }
                Class<?> encoderClass = compiler.compile("gen.GeneratedEncoder", src);
                return (Encoder) encoderClass.newInstance();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DsonEncodeException(e);
            }
        }
        throw new UnsupportedOperationException("not implemented");
    }
}
