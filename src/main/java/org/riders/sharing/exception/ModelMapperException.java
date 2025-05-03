package org.riders.sharing.exception;

public class ModelMapperException extends RuntimeException {
    public ModelMapperException(String message) {
        super(message);
    }

    public ModelMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
