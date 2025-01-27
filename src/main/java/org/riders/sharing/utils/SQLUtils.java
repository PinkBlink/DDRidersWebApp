package org.riders.sharing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.utils.constants.DataBaseInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLUtils {
    private static final Logger logger = LogManager.getLogger(SQLUtils.class);

    public static void initDatabase() {
        if (SQLValidator.isCreatedDB(DataBaseInfo.DD_RIDERS_URL, DataBaseInfo.USER, DataBaseInfo.PASSWORD)) {
            logger.info("Database dd_riders_db has already been created.;");
        } else {
            sendCreateFile(DataBaseInfo.PATH_TO_CREATE_DATABASE_FILE
                    , DataBaseInfo.POSTGRES_URL
                    , DataBaseInfo.USER
                    , DataBaseInfo.PASSWORD);

            sendCreateFile(DataBaseInfo.PATH_TO_CREATE_TABLES_FILE
                    , DataBaseInfo.DD_RIDERS_URL
                    , DataBaseInfo.USER
                    , DataBaseInfo.PASSWORD);

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
            logger.error("File doesn't exist. Path " + DataBaseInfo.PATH_TO_CREATE_DATABASE_FILE, e);
            throw new RuntimeException(e);
        }
    }
}