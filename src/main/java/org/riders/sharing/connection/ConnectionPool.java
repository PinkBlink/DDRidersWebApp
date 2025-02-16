package org.riders.sharing.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.ConnectionException;
import org.riders.sharing.utils.SQLUtils;
import org.riders.sharing.utils.constants.DataBaseInfo;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public enum ConnectionPool {
    INSTANCE;
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    private static final ReentrantLock lock = new ReentrantLock();
    private static final int DEFAULT_CAPACITY = 16;
    private Queue<Connection> availableConnections;
    private Queue<Connection> busyConnections;


    ConnectionPool() {
        registerDriver();
        SQLUtils.initDatabase();
        initConnections();
    }
    private void registerDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
                        throw new RuntimeException(e);
                    }
                });
    }

    private void initConnections() {
        availableConnections = new ArrayDeque<>(DEFAULT_CAPACITY);
        busyConnections = new ArrayDeque<>();
        try {
            for (int i = 0; i < DEFAULT_CAPACITY; i++) {
                Connection connection = createConnection();
                availableConnections.add(connection);
            }
        } catch (ConnectionException e) {
            logger.error(e.getMessage());
        }
        if (availableConnections.isEmpty()) {
            logger.error("Available connections amount is zero;");
            throw new RuntimeException("No connections to database");
        } else {
            logger.info("Connections successfully created: " + availableConnections.size());
        }

    }

    private Connection createConnection() throws ConnectionException {
            Connection connection;
            try {
                connection = DriverManager.getConnection(DataBaseInfo.DD_RIDERS_URL, DataBaseInfo.USER, DataBaseInfo.PASSWORD);
                logger.info("Create new connection: " + connection.toString());

            } catch (SQLException e) {
                logger.error("Can't create connection: " + e);
                throw new ConnectionException(e.getMessage());
            }
            return connection;
    }

    public Connection getConnection() {
        lock.lock();
        try {
            Connection connection;
            connection = availableConnections.peek();
            busyConnections.offer(connection);

            return connection;
        }finally {
            lock.unlock();
        }
    }

    public void releaseConnection(Connection connection) {
        lock.lock();
        try {
            busyConnections.remove(connection);
            availableConnections.offer(connection);
        }finally {
            lock.unlock();
        }
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
