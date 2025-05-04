package org.riders.sharing.exception;

public class ResponseWritingException extends RuntimeException{
    public ResponseWritingException(String message) {
        super(message);
    }

    public ResponseWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
