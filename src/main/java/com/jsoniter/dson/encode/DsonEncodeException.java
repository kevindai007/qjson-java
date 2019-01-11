package com.jsoniter.dson.encode;

public class DsonEncodeException extends RuntimeException {

    public DsonEncodeException() {
    }

    public DsonEncodeException(String message) {
        super(message);
    }

    public DsonEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DsonEncodeException(Throwable cause) {
        super(cause);
    }

    public DsonEncodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
