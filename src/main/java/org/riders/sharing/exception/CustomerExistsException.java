package org.riders.sharing.exception;

public class CustomerExistsException extends RuntimeException{
    public CustomerExistsException(String message) {
        super(message);
    }

    public CustomerExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
