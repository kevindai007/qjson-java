package com.jsoniter.dson.any;

import java.util.ArrayList;

public class AnyList<E> extends ArrayList<E> implements Any {

    public AnyList(E... elements) {
        super(elements.length);
        for (E element : elements) {
            add(element);
        }
    }

    @Override
    public Object get(Object... path) {
        return null;
    }
}
