package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.config.ApplicationConfig;
import org.riders.sharing.exception.SQLFileNotFoundException;
import org.riders.sharing.exception.NoSQLConnectionException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlUtils {
    public static final String DUPLICATE_ENTRY_SQL_ERR_CODE = "23505";

    private static final Logger logger = LogManager.getLogger(SqlUtils.class);

    public static void initDatabase(ApplicationConfig applicationConfig) {
        if (!isDatabaseCreated(applicationConfig.getDdRidersDbUrl(),
            applicationConfig.getUser(),
            applicationConfig.getPassword())) {

            logger.info("Attempt to send create db file {}", applicationConfig.getDdRidersDbUrl());
            executeSqlScriptFromFile(
                applicationConfig.getPathToCreateDbScript(),
                applicationConfig.getPostgresDbUrl(),
                applicationConfig.getUser(),
                applicationConfig.getPassword()
            );

            logger.info(
                "Attempt to send create tables file: {}",
                applicationConfig.getPathToCreateTablesScript()
            );

            executeSqlScriptFromFile(
                applicationConfig.getPathToCreateTablesScript(),
                applicationConfig.getDdRidersDbUrl(),
                applicationConfig.getUser(),
                applicationConfig.getPassword()
            );

            logger.info("Database and tables are successfully created");
        } else {
            logger.info("{} is already exists", applicationConfig.getDdRidersDbUrl());
        }
    }

    private static void executeSqlScriptFromFile(String path, String url, String user, String password) {
        try (final var connection = DriverManager.getConnection(url, user, password)) {
            logger.info("Created connection with {}", url);

            final var inputStream = getInputStreamFromFile(path);
            final var statement = connection.createStatement();
            final var createScript = getStringFromInputStream(inputStream);

            statement.execute(createScript);
            statement.close();

            logger.info("The script is successfully sent;");
        } catch (SQLException e) {
            logger.error("Couldn't create connection to database with URL: {}", url, e);
            throw new NoSQLConnectionException(
                "Couldn't create connection to database with URL:" + url, e);
        }
    }

    private static InputStream getInputStreamFromFile(String path) {
        final var inputStream = SqlUtils.class.getClassLoader().getResourceAsStream(path);

        if (inputStream == null) {
            logger.error("File doesn't exist. Path: {}", path);
            throw new SQLFileNotFoundException("File doesn't exist. Path: " + path);
        }

        return inputStream;
    }

    private static String getStringFromInputStream(InputStream inputStream) {

        final var stringBuilder = new StringBuilder();
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        bufferedReader.lines().forEach(
            line -> stringBuilder.append(line).append("\n")
        );

        return stringBuilder.toString().trim();
    }

    private static boolean isDatabaseCreated(String url, String user, String password) {
        try (final var connectionCheck = DriverManager.getConnection(url, user, password)) {
            return true;
        } catch (SQLException e) {
            logger.warn("Database is not created. URL: {}", url);
            return false;
        }
    }
}
