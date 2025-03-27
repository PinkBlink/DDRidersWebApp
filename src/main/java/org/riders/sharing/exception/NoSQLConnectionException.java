package org.riders.sharing.exception;

public class NoSQLConnectionException extends RuntimeException {
    public NoSQLConnectionException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public NoSQLConnectionException(String reason) {
        super(reason);
    }
}
