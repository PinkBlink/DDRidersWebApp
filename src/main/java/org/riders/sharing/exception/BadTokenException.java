package org.riders.sharing.exception;

public class BadTokenException extends RuntimeException{
    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
