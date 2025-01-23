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
    private static Logger logger = LogManager.getLogger(SQLUtils.class);

    public static void initDatabase() {
        if (!SQLValidator.isCreatedDB()) {
            try (Connection connection = DriverManager.getConnection(DataBaseInfo.INITIAL_URL
                    , DataBaseInfo.USER
                    , DataBaseInfo.PASSWORD)) {
                Statement statement = connection.createStatement();
                String createScript = getCreateScript();
                statement.execute(createScript);
                statement.close();
            } catch (SQLException e) {
                logger.error("Can't connect to :" + DataBaseInfo.INITIAL_URL);
                throw new RuntimeException(e);
            }
        }
    }

    private static String getCreateScript() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> script = Files.readAllLines(Path.of(DataBaseInfo.PATH_TO_DATABASE_CREATE_FILE));
            for (String line : script) {
                stringBuilder.append(line.trim())
                        .append("\n");
            }
            return stringBuilder.toString().trim();

        } catch (IOException e) {
            logger.error("File doesn't exist. Path " + DataBaseInfo.PATH_TO_DATABASE_CREATE_FILE, e);
            throw new RuntimeException(e);
        }
    }
}