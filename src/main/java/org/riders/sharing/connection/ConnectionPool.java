package org.riders.sharing.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.NoSQLConnectionException;
import org.riders.sharing.utils.SqlUtils;
import org.riders.sharing.utils.constants.DatabaseInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

public enum ConnectionPool {
    INSTANCE;
    private static final int DEFAULT_CAPACITY = 16;

    private final Logger logger;
    private DatabaseInitParams databaseInitParams;
    private Queue<Connection> availableConnections;
    private Queue<Connection> busyConnections;

    ConnectionPool() {
        logger = LogManager.getLogger(ConnectionPool.class);
        databaseInitParams = DatabaseInfo.DD_RIDERS_DATABASE_INIT_PARAMS;
        registerDriver();
        SqlUtils.initDatabase(databaseInitParams);
        initConnections();
    }

    private void registerDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new NoSQLConnectionException(e.getMessage());
        }
    }

    private void deregisterDriver() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Can't deregister driver {}", e.getMessage());
                throw new NoSQLConnectionException(e.getMessage());
            }
        });
    }

    private void initConnections() {
        availableConnections = new ArrayDeque<>(DEFAULT_CAPACITY);
        busyConnections = new ArrayDeque<>();

        try {
            for (int i = 0; i < DEFAULT_CAPACITY; i++) {
                final var connection = createConnection(databaseInitParams.customDBUrl(),
                    databaseInitParams.user(), databaseInitParams.password());

                availableConnections.add(connection);
            }
        } catch (NoSQLConnectionException e) {
            logger.error(e.getMessage());
        }

        if (availableConnections.isEmpty()) {
            logger.error("Available connections amount is zero;");
            throw new RuntimeException("No connections to database");
        } else {
            logger.info("Connections successfully created: {}", availableConnections.size());
        }
    }

    private Connection createConnection(String url, String user, String password)
        throws NoSQLConnectionException {
        try {
            final var connection = DriverManager.getConnection(url, user, password);
            logger.info("Create new connection: {}", connection.toString());
            return connection;
        } catch (SQLException e) {
            logger.error("Can't create connection: {}", e.getMessage());
            throw new NoSQLConnectionException(e.getMessage(), e);
        }
    }

    public ConnectionPool setDatabaseInitParams(DatabaseInitParams databaseInitParams) {
        this.databaseInitParams = databaseInitParams;
        SqlUtils.initDatabase(databaseInitParams);
        initConnections();
        return INSTANCE;
    }

    public synchronized Connection getConnection() {
        final var connection = availableConnections.peek();
        busyConnections.offer(connection);

        return connection;
    }

    public void releaseConnection(Connection connection) {
        busyConnections.remove(connection);
        availableConnections.offer(connection);
    }

    public void destroyPool() {
        try {
            for (final var connection : availableConnections) {
                connection.close();
            }

            for (final var connection : busyConnections) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Can't close connection");
            throw new NoSQLConnectionException("Can't close connection", e);
        }
        deregisterDriver();
    }
}
