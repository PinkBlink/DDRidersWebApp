package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.SQLFileNotFoundException;
import org.riders.sharing.exception.NoSQLConnectionException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import static org.riders.sharing.utils.constants.DataBaseInfo.*;

public class SQLUtils {
    private static final Logger logger = LogManager.getLogger(SQLUtils.class);

    public static void initDatabase() {
        if (!isDatabaseCreated(DD_RIDERS_URL, USER, PASSWORD)) {
            logger.info("Attempt to send create db file");
            sendCreateFile(PATH_TO_CREATE_DATABASE_FILE
                    , POSTGRES_URL
                    , USER
                    , PASSWORD);

            logger.info("Attempt to send create tables file");
            sendCreateFile(PATH_TO_CREATE_TABLES_FILE
                    , DD_RIDERS_URL
                    , USER
                    , PASSWORD);

            logger.info("Database and tables are successfully created");
        } else {
            logger.info("dd_riders_db is already exists");
        }
    }

    private static void sendCreateFile(String path, String url, String user, String password) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            logger.info("Created connection with {}", url);

            InputStream inputStream = getInputStreamFromFile(path);
            Statement statement = connection.createStatement();
            String createScript = getStringFromInputStream(inputStream);

            statement.execute(createScript);
            statement.close();

            logger.info("The script is successfully sent;");
        } catch (SQLException e) {
            logger.error("Couldn't create connection to database with URL: {}", url, e);
            throw new NoSQLConnectionException("Couldn't create connection to database with URL:" + url, e);
        }
    }

    private static InputStream getInputStreamFromFile(String path) {
        InputStream inputStream = SQLUtils.class.getClassLoader().getResourceAsStream(path);

        if (inputStream == null) {
            logger.error("File doesn't exist. Path: {}", path);
            throw new SQLFileNotFoundException("File doesn't exist. Path: " + path);
        }

        return inputStream;
    }

    private static String getStringFromInputStream(InputStream inputStream) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        bufferedReader.lines().forEach(line -> stringBuilder.append(line).append("\n"));

        return stringBuilder.toString().trim();
    }

    private static boolean isDatabaseCreated(String url, String user, String password) {
        try (Connection maybeConnection = DriverManager.getConnection(url, user, password)) {
            return true;
        } catch (SQLException e) {
            logger.warn("Database is not created. URL: {}", url);
            return false;
        }
    }
}
