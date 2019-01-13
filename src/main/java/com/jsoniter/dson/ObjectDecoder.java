package com.jsoniter.dson;

import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

class ObjectDecoder implements Decoder  {

    private Decoder listDecoder;
    private Decoder mapDecoder;

    public void init(Decoder listDecoder, Decoder mapDecoder) {
        this.listDecoder = listDecoder;
        this.mapDecoder = mapDecoder;
    }

    @Override
    public Object decode(DecoderSource source) {
        byte b = source.peek();
        switch (b) {
            case '[':
                return listDecoder.decode(source);
            case '{':
                return mapDecoder.decode(source);
            case 't':
            case 'f':
                return source.decodeBoolean();
            case 'n':
                if (source.decodeNull()) {
                    return null;
                }
                throw source.reportError("expect null");
            case '"':
                return source.decodeStringOrNumber();
            default:
                throw source.reportError("unexpected token");
        }
    }
}
