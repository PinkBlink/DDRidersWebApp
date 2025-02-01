package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.utils.constants.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

import static org.riders.sharing.utils.constants.DataBaseInfo.*;

public class SQLUtils {
    private static final Logger logger = LogManager.getLogger(SQLUtils.class);

    public static void initDatabase() { // i should rework those tasty spaghetti
        if (SQLValidator.isCreatedDB(DD_RIDERS_URL, USER, PASSWORD)) {
            logger.info("Database dd_riders_db has already been created;");
            try (Connection connection = DriverManager.getConnection(DD_RIDERS_URL, USER, PASSWORD)) {

                if (SQLValidator.isTableCreated(connection, CustomerSQLColumns.TABLE_NAME.getName())
                        && SQLValidator.isTableCreated(connection, ScooterSQLColumns.TABLE_NAME.getName())
                        && SQLValidator.isTableCreated(connection, OrderSQLColumns.TABLE_NAME.getName())) {
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

    private static void sendCreateFile(String path, String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {
            logger.info("Created connection with " + URL);

            Statement statement = connection.createStatement();
            String createScript = getStringCreateScriptFromFile(path);
            statement.execute(createScript);
            statement.close();
            logger.info("Script is sent successfully;");
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
            throw new RuntimeException(e);
        }
    }
}