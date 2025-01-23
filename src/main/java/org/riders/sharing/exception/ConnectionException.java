package org.riders.sharing.exception;

import java.sql.SQLException;

public class ConnectionException extends SQLException {
    public ConnectionException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public ConnectionException(String reason) {
        super(reason);
    }
}
