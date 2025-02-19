package org.riders.sharing.utils;

import java.sql.*;

public class SQLValidator {
    public static boolean isCreatedDB(String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isTableCreated(Connection connection, String tableName) throws SQLException {
        PreparedStatement statement = null;
        try {
            String query = "SELECT * FROM " + tableName;
            statement = connection.prepareStatement(query);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
