package org.riders.sharing.exception;

public class WrongEmailOrPasswordException extends RuntimeException {
    public WrongEmailOrPasswordException(String message) {
        super(message);
    }

    public WrongEmailOrPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
