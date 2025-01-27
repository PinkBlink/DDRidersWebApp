package org.riders.sharing.utils;

import java.sql.*;

public class SQLValidator {
    public static boolean isCreatedDB(String URL, String user, String password) {
        try (Connection connection = DriverManager.getConnection(URL
                , user
                , password)) {
            connection.createStatement()
                    .executeQuery("SELECT * FROM Customers LIMIT 1;")
                    .close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}