package org.riders.sharing.repository;

import org.apache.logging.log4j.LogManager;
import org.riders.sharing.model.BaseEntity;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends BaseEntity> {
    T save(T entity);

    T update(T entity);

    Optional<T> find(UUID id);

    List<T> findAll();

    boolean isExist(T entity);

    T delete(UUID id);

    default void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LogManager.getLogger(this).error("Can't close statement", e);
            }
        }
    }
}
