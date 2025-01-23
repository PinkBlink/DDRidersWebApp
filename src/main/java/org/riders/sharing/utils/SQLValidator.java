package org.riders.sharing.utils;

import org.riders.sharing.utils.constants.DataBaseInfo;

import java.sql.*;

public class SQLValidator {
    public static boolean isCreatedDB() {
        try (Connection connection = DriverManager.getConnection(DataBaseInfo.URL
                , DataBaseInfo.USER
                , DataBaseInfo.PASSWORD)) {
            connection.createStatement()
                    .executeQuery("SELECT * FROM Customers LIMIT 1;")
                    .close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
