package com.jsoniter.dson.marshal;

public class DsonMarshalException extends RuntimeException {

    public DsonMarshalException() {
    }

    public DsonMarshalException(String message) {
        super(message);
    }

    public DsonMarshalException(String message, Throwable cause) {
        super(message, cause);
    }

    public DsonMarshalException(Throwable cause) {
        super(cause);
    }

    public DsonMarshalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
