package com.jsoniter.dson.codegen;

import com.jsoniter.dson.codegen.gen.Gen;
import com.jsoniter.dson.codegen.gen.Indent;
import com.jsoniter.dson.codegen.gen.Line;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

import java.lang.reflect.Type;
import java.util.function.Function;

public interface ArrayDecoder {

    interface Helper {

        static Decoder getElemDecoder(Function<Type, Decoder> decoderProvider, Class clazz) {
            Decoder decoder = decoderProvider.apply(clazz.getComponentType());
            return decoder;
        }

        static void expectArrayHead(DecoderSource source) {
            byte b = source.peek();
            if (b != '[') {
                throw source.reportError("expect [");
            }
            source.next();
        }

        static void expectArrayTail(DecoderSource source, byte b) {
            if (b != ']') {
                throw source.reportError("expect ]");
            }
            source.next();
        }
    }

    static void $(Gen g, String decoderClassName, Class clazz) {
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" elemDecoder;"));
        GenDecoder.ctor(g, decoderClassName, new Indent(() -> {
            g.__("this.elemDecoder = "
            ).__(Helper.class.getCanonicalName()
            ).__(new Line(".getElemDecoder(decoderProvider, clazz);"));
        }));
        GenDecoder.method(g, new Indent(() -> {
            g.__(Helper.class.getCanonicalName()).__(new Line(".expectArrayHead(source);"));
            // if array is []
            g.__(new Line("byte b = source.peek();"));
            g.__("if (b == ']') { "
            ).__(new Indent(() -> {
                g.__(new Line("source.next();"));
                g.__("return new "
                ).__(clazz.getCanonicalName()
                ).__("{};");
            })).__(new Line("}"));
            // init arr
            if (clazz.getComponentType().isPrimitive()) {
                g.__(clazz.getCanonicalName()
                ).__(" arr = new "
                ).__(clazz.getComponentType().getCanonicalName()
                ).__(new Line("[4];"));
            } else {
                g.__(clazz.getCanonicalName()
                ).__(" arr = new "
                ).__(clazz.getCanonicalName()
                ).__(new Line("{null,null,null,null};"));
            }
            // loop to read
            g.__(new Line("int i = 0;"));
            g.__("do {"
            ).__(new Indent(() -> {
                g.__("arr[i++] = ("
                ).__(clazz.getComponentType().getCanonicalName()
                ).__(new Line(")elemDecoder.decode(source);"));
                g.__(new Line("if (i == arr.length) { arr = java.util.Arrays.copyOf(arr, arr.length * 2); }"));
            })).__(new Line("} while((b = source.read()) == ',');"));
            g.__(Helper.class.getCanonicalName()).__(new Line(".expectArrayTail(source, b);"));
            g.__(new Line("return java.util.Arrays.copyOf(arr, i);"));
        }));
    }
}
