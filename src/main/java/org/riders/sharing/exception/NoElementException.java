package org.riders.sharing.exception;

public class NoElementException extends RuntimeException {
    public NoElementException(String message) {
        super(message);
    }

    public NoElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
