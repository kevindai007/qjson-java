package org.qjson.demo.inf;

public class My {

    public interface Inf {
        String getField();
    }

    // package scope
    private static class PrivateClass implements Inf {

        String field;

        @Override
        public String getField() {
            return field;
        }
    }

    public static Object newObject() {
        PrivateClass obj = new PrivateClass();
        obj.field = "hello";
        return obj;
    }
}
