package org.riders.sharing.exception;

public class BadDatabaseUpdateException extends RuntimeException{
    public BadDatabaseUpdateException(String message) {
        super(message);
    }

    public BadDatabaseUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
