package org.riders.sharing.exception;

public class IllegalStatusException extends RuntimeException{
    public IllegalStatusException(String message) {
        super(message);
    }

    public IllegalStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
