package org.qjson.decode;

public class DsonDecodeException extends RuntimeException {

    public DsonDecodeException() {
    }

    public DsonDecodeException(String message) {
        super(message);
    }

    public DsonDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DsonDecodeException(Throwable cause) {
        super(cause);
    }

    public DsonDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
