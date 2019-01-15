package org.qjson.codegen;

import org.qjson.codegen.gen.Gen;
import org.qjson.codegen.gen.Indent;
import org.qjson.codegen.gen.Line;
import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;
import org.qjson.spi.QJsonSpi;
import org.qjson.spi.TypeVariables;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionDecoderGenerator implements Generator {

    @Override
    public Map<String, Object> args(Codegen.Config cfg, QJsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        TypeVariable typeParam = Collection.class.getTypeParameters()[0];
        Type elemType = TypeVariables.substitute(typeParam, typeArgs);
        Decoder elemDecoder = spi.decoderOf(elemType);
        return new HashMap<String, Object>() {{
            put("elemDecoder", elemDecoder);
        }};
    }

    @Override
    public void genFields(Gen g, Map<String, Object> args) {
        g.__("private final "
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(" elemDecoder;"));
    }

    @Override
    public void genCtor(Gen g, Map<String, Object> args) {
        g.__("this.elemDecoder = ("
        ).__(Decoder.class.getCanonicalName()
        ).__(new Line(")args.get(\"elemDecoder\");"));
    }

    @Override
    public void genMethod(Gen g, Map<String, Object> args, Class clazz) {
        g.__(ArrayDecoderGenerator.Helper.class.getCanonicalName()).__(new Line(".expectArrayHead(source);"));
        // if collection is []
        g.__("java.util.Collection col = new "
        ).__(clazz.getCanonicalName()
        ).__(new Line("();"));
        g.__("if (source.peek() == ']') { "
        ).__(new Indent(() -> {
            g.__(new Line("source.next();"));
            g.__(new Line("return col;"));
        })).__(new Line("}"));
        // fill collection
        g.__(Helper.class.getCanonicalName()).__(new Line(".fill(source, elemDecoder, col);"));
        g.__(new Line("return col;"));
    }

    public interface Helper {

        static void fill(DecoderSource source, Decoder elemDecoder, Collection col) {
            byte b;
            do {
                col.add(elemDecoder.decode(source));
            } while ((b = source.read()) == ',');
            if (b != ']') {
                throw source.reportError("expect ]");
            }
        }
    }
}
