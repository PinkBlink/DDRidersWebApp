package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.FileNotFoundException;
import org.riders.sharing.exception.NoSQLConnectionException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import static org.riders.sharing.utils.constants.DataBaseInfo.*;

public class SQLUtils {
    private static final Logger logger = LogManager.getLogger(SQLUtils.class);

    public static void initDatabase() {
        if (isCreatedDB(DD_RIDERS_URL, USER, PASSWORD)) {
            logger.info("dd_riders_db is already exists");
            createTypesIfNotExists();
            sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                    , DD_RIDERS_URL
                    , USER
                    , PASSWORD);

        } else {
            logger.info("Attempt to send create db file");
            sendCreateFile(PATH_TO_CREATE_DATABASE_FILE
                    , POSTGRES_URL
                    , USER
                    , PASSWORD);

            logger.info("Attempt to send create types file");
            createTypesIfNotExists();

            logger.info("Attempt to send create tables file");
            sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                    , DD_RIDERS_URL
                    , USER
                    , PASSWORD);

            logger.info("Database and tables are successfully created");
        }
    }

    private static void createTypesIfNotExists() {
        if (!isTypesExists()) {
            logger.info("Attempt to send create types file;");
            sendCreateFile(PATH_TO_CREATE_TYPES_FILE, DD_RIDERS_URL, USER, PASSWORD);
        }
    }

    private static void sendCreateFile(String path, String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {
            logger.info("Created connection with " + URL);

            InputStream inputStream = getInputStreamFromFile(path);
            Statement statement = connection.createStatement();
            String createScript = getStringFromInputStream(inputStream);

            statement.execute(createScript);
            statement.close();

            logger.info("The script is successfully sent;");
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException();
        }
    }

    private static InputStream getInputStreamFromFile(String path) {
        InputStream inputStream = SQLUtils.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            logger.error("File doesn't exist. Path: " + path);
            throw new FileNotFoundException("File doesn't exist. Path: " + path);
        }
        return inputStream;
    }

    private static String getStringFromInputStream(InputStream inputStream) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        bufferedReader.lines().forEach(line -> stringBuilder.append(line).append("\n"));

        return stringBuilder.toString().trim();
    }

    private static boolean isTypesExists() {
        String scooterType = "SELECT 1 FROM pg_type WHERE typname = 'scooter_type'; ";
        String scooterStatus = "SELECT 1 FROM pg_type WHERE typname = 'scooter_status'; ";
        String orderStatus = "SELECT 1 FROM pg_type WHERE typname = 'scooter_status'; ";

        try (Connection connection = DriverManager.getConnection(DD_RIDERS_URL, USER, PASSWORD);
             PreparedStatement statementScooterStatus = connection.prepareStatement(scooterStatus);
             PreparedStatement statementScooterType = connection.prepareStatement(scooterType);
             PreparedStatement statementOrderStatus = connection.prepareStatement(orderStatus)) {

            boolean isScooterStatus = statementScooterStatus.executeQuery().next();
            boolean isScooterType = statementScooterType.executeQuery().next();
            boolean isOrderStatus = statementOrderStatus.executeQuery().next();

            return isOrderStatus && isScooterType && isScooterStatus;
        } catch (SQLException e) {
            throw new NoSQLConnectionException("Error occurred while trying to check sql types in database", e);
        }
    }

    private static boolean isCreatedDB(String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
