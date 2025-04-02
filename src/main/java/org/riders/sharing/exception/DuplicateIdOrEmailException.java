package org.riders.sharing.exception;

public class DuplicateIdOrEmailException extends RuntimeException{
    public DuplicateIdOrEmailException(String message) {
        super(message);
    }

    public DuplicateIdOrEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
