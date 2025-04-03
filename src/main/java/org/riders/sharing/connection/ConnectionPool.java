package org.riders.sharing.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.NoSQLConnectionException;
import org.riders.sharing.utils.SQLUtils;
import org.riders.sharing.utils.constants.DataBaseInfo;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.Queue;

public enum ConnectionPool {
    INSTANCE;
    private static final int DEFAULT_CAPACITY = 16;

    private final Logger logger;
    private Queue<Connection> availableConnections;
    private Queue<Connection> busyConnections;

    ConnectionPool() {
        logger = LogManager.getLogger(ConnectionPool.class);
        registerDriver();
        SQLUtils.initDatabase();
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
        DriverManager.getDrivers()
                .asIterator()
                .forEachRemaining(driver -> {
                    try {
                        DriverManager.deregisterDriver(driver);
                    } catch (SQLException e) {
                        logger.error("Can't deregister driver\n" + e);
                        throw new NoSQLConnectionException(e.getMessage());
                    }
                });
    }

    private void initConnections() {
        initConnections(DataBaseInfo.DD_RIDERS_URL, DataBaseInfo.USER, DataBaseInfo.PASSWORD);
    }

    private void initConnections(String url, String user, String password) {
        availableConnections = new ArrayDeque<>(DEFAULT_CAPACITY);
        busyConnections = new ArrayDeque<>();

        try {
            for (int i = 0; i < DEFAULT_CAPACITY; i++) {
                Connection connection = createConnection(url, user, password);
                availableConnections.add(connection);
            }
        } catch (NoSQLConnectionException e) {
            logger.error(e.getMessage());
        }

        if (availableConnections.isEmpty()) {
            logger.error("Available connections amount is zero;");
            throw new RuntimeException("No connections to database");
        } else {
            logger.info("Connections successfully created: " + availableConnections.size());
        }
    }

    private Connection createConnection(String url, String user, String password) throws NoSQLConnectionException {
        Connection connection;

        try {
            connection = DriverManager.getConnection(url, user, password);
            logger.info("Create new connection: " + connection.toString());
        } catch (SQLException e) {
            logger.error("Can't create connection: " + e);
            throw new NoSQLConnectionException(e.getMessage());
        }
        return connection;
    }

    public void setDatabaseURL(String url, String user, String password) {
        initConnections(url, user, password);
    }

    public synchronized Connection getConnection() {
        Connection connection;
        connection = availableConnections.peek();
        busyConnections.offer(connection);

        return connection;
    }

    public void releaseConnection(Connection connection) {
        busyConnections.remove(connection);
        availableConnections.offer(connection);
    }

    public void destroyPool() {
        try {
            for (Connection connection : availableConnections) {
                connection.close();
            }

            for (Connection connection : busyConnections) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Can't close connection");
            throw new RuntimeException(e);
        }
        deregisterDriver();
    }
}
