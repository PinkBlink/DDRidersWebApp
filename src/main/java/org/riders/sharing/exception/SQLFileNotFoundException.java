package org.riders.sharing.exception;

public class SQLFileNotFoundException extends RuntimeException {
    public SQLFileNotFoundException(String message) {
        super(message);
    }

    public SQLFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
