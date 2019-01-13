package com.jsoniter.dson;

import com.jsoniter.dson.any.Any;
import com.jsoniter.dson.any.AnyObject;
import com.jsoniter.dson.spi.Decoder;
import com.jsoniter.dson.spi.DecoderSource;

class AnyDecoder implements Decoder {

    private final Decoder objectDecoder;

    AnyDecoder(Decoder objectDecoder) {
        this.objectDecoder = objectDecoder;
    }

    @Override
    public Object decode(DecoderSource source) {
        Object obj = objectDecoder.decode(source);
        if (obj instanceof Any) {
            return obj;
        }
        return new AnyObject(obj);
    }
}
