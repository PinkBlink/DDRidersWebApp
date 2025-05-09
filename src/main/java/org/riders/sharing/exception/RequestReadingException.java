package org.riders.sharing.exception;

public class RequestReadingException extends RuntimeException{
    public RequestReadingException(String message) {
        super(message);
    }

    public RequestReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
