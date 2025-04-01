package org.riders.sharing.exception;

public class WrongCustomerException extends RuntimeException{
    public WrongCustomerException(String message) {
        super(message);
    }

    public WrongCustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
