package org.qjson;

import org.qjson.spi.Decoder;
import org.qjson.spi.DecoderSource;
import org.qjson.spi.QJsonSpi;
import org.qjson.spi.TypeVariables;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class CollectionDecoder implements Decoder {

    private final Function<DecoderSource, Object> colFactory;
    private final Decoder elemDecoder;

    public CollectionDecoder(Function<DecoderSource, Object> colFactory, Decoder elemDecoder) {
        this.colFactory = colFactory;
        this.elemDecoder = elemDecoder;
    }

    public static Decoder create(QJsonSpi spi, Class clazz, Map<TypeVariable, Type> typeArgs) {
        Function<DecoderSource, Object> colFactory = spi.factoryOf(clazz);
        TypeVariable typeParam = Collection.class.getTypeParameters()[0];
        Type elemType = TypeVariables.substitute(typeParam, typeArgs);
        Decoder elemDecoder = spi.decoderOf(elemType);
        return new CollectionDecoder(colFactory, elemDecoder);
    }

    @Override
    public Object decode(DecoderSource source) {
        byte b = source.peek();
        if (b != '[') {
            throw source.reportError("expect [");
        }
        source.next();
        Collection col = (Collection) colFactory.apply(source);
        if (source.peek() == ']') {
            source.next();
            return col;
        }
        do {
            col.add(source.decodeObject(elemDecoder));
        } while ((b = source.read()) == ',');
        if (b != ']') {
            throw source.reportError("expect ]");
        }
        return col;
    }
}
