package com.jsoniter.dson.unmarshal;

public class DsonUnmarshalException extends RuntimeException {

    public DsonUnmarshalException() {
    }

    public DsonUnmarshalException(String message) {
        super(message);
    }

    public DsonUnmarshalException(String message, Throwable cause) {
        super(message, cause);
    }

    public DsonUnmarshalException(Throwable cause) {
        super(cause);
    }

    public DsonUnmarshalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
