package org.riders.sharing.repository;

import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.sql.Statement;

public interface BaseRepository {

    default void closeStatement(Statement statement){
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LogManager.getLogger(this).error("Can't close statement", e);
            }
        }
    }
}
