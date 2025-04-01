package org.riders.sharing.exception;

public class BadDatabaseSelectException extends RuntimeException{
    public BadDatabaseSelectException(String message) {
        super(message);
    }

    public BadDatabaseSelectException(String message, Throwable cause) {
        super(message, cause);
    }
}
