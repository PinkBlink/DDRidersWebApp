package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.FileNotFoundException;
import org.riders.sharing.exception.NoSQLConnectionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

import static org.riders.sharing.utils.constants.DataBaseInfo.*;

public class SQLUtils {
    private static final Logger logger = LogManager.getLogger(SQLUtils.class);
    private static final String CUSTOMERS_TABLE_NAME = "customers";
    private static final String SCOOTERS_TABLE_NAME = "scooters";
    private static final String ORDERS_TABLE_NAME = "orders";

    public static void initDatabase() {
        if (SQLValidator.isCreatedDB(DD_RIDERS_URL, USER, PASSWORD)) {
            createTypesIfNotExists();
            sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                    , DD_RIDERS_URL
                    , USER
                    , PASSWORD);

        } else {
            sendCreateFile(PATH_TO_CREATE_DATABASE_FILE
                    , POSTGRES_URL
                    , USER
                    , PASSWORD);

            sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                    , DD_RIDERS_URL
                    , USER
                    , PASSWORD);

            logger.info("Database and tables are successfully created");
        }
    }

    private static void createTablesIfNotExists() {
        try (Connection connection = DriverManager.getConnection(DD_RIDERS_URL, USER, PASSWORD)) {
            if (SQLValidator.isTableCreated(connection, CUSTOMERS_TABLE_NAME)
                    && SQLValidator.isTableCreated(connection, SCOOTERS_TABLE_NAME)
                    && SQLValidator.isTableCreated(connection, ORDERS_TABLE_NAME)) {
                logger.info("Tables have already been created;");

            } else {

                logger.info("Database is created without tables. Attempt to create tables");
                sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                        , DD_RIDERS_URL
                        , USER
                        , PASSWORD);
                logger.info("Tables were successfully created");
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to create tables;", e);
            throw new RuntimeException(e);
        }
    }

    private static void createTypesIfNotExists() {
        if (!isTypesExists()) {
            sendCreateFile(PATH_TO_CREATE_TYPES_FILE, DD_RIDERS_URL, USER, PASSWORD);
        }
    }

    private static void sendCreateFile(String path, String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {

            logger.info("Created connection with " + URL);

            Statement statement = connection.createStatement();
            String createScript = getStringCreateScriptFromFile(path);
            statement.execute(createScript);
            statement.close();
            logger.info("The script is successfully sent;");
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException();
        }
    }

    private static String getStringCreateScriptFromFile(String path) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> script = Files.readAllLines(Path.of(path));
            for (String line : script) {
                stringBuilder.append(line.trim()).append("\n");
            }
            return stringBuilder.toString().trim();

        } catch (IOException e) {
            logger.error("File doesn't exist. Path: " + path, e);
            throw new FileNotFoundException("File doesn't exist. Path: " + path, e);
        }
    }

    private static boolean isTypesExists() {
        String scooterType = "SELECT 1" +
                "FROM pg_type" +
                "WHERE typname = 'scooter_type';";
        String scooterStatus = "SELECT 1" +
                "FROM pg_type" +
                "WHERE typname = 'scooter_status';";
        String orderStatus = "SELECT 1" +
                "FROM pg_type" +
                "WHERE typname = 'scooter_status';";

        try (Connection connection = DriverManager.getConnection(DD_RIDERS_URL, USER, PASSWORD);
             PreparedStatement statementScooterStatus = connection.prepareStatement(scooterStatus);
             PreparedStatement statementScooterType = connection.prepareStatement(scooterType);
             PreparedStatement statementOrderStatus = connection.prepareStatement(orderStatus)) {

            boolean isScooterStatus = statementScooterStatus.executeQuery().next();
            boolean isScooterType = statementScooterType.executeQuery().next();
            boolean isOrderStatus = statementOrderStatus.executeQuery().next();
            return isOrderStatus && isScooterType && isScooterStatus;
        } catch (SQLException e) {
            throw new NoSQLConnectionException("Error occurred while trying to check sql types");
        }
    }
}
