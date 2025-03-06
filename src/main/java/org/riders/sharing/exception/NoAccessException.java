package org.riders.sharing.exception;

public class NoAccessException extends RuntimeException{
    public NoAccessException(String message) {
        super(message);
    }

    public NoAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
