package org.riders.sharing.repository.impl;

import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.ScooterRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScooterRepositoryImpl implements ScooterRepository {
    private final ConnectionPool connectionPool = ConnectionPool.INSTANCE;

    @Override
    public Scooter save(Scooter scooter) {
        String query = """
                INSERT INTO scooters(id, create_time, update_time, scooter_type, scooter_status, battery_level)
                VALUES( ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            Scooter scooterToStore = scooter.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            preparedStatement.setObject(1, scooterToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(scooterToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(scooterToStore.getUpdateTime()));
            preparedStatement.setObject(4, scooterToStore.getScooterType(), Types.OTHER);
            preparedStatement.setObject(5, scooterToStore.getStatus(), Types.OTHER);
            preparedStatement.setInt(6, scooterToStore.getBatteryLevel());
            preparedStatement.executeUpdate();

            return scooterToStore;
        } catch (SQLException e) {
            throw new DuplicateEntryException(e.getMessage());
        }
    }

    @Override
    public Scooter update(Scooter scooter) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Scooter scooterToStore = scooter.toBuilder()
                    .setUpdateTime(Instant.now())
                    .build();
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            UPDATE scooters
                            SET update_time = ?,
                            scooter_type = ?,
                            scooter_status = ?,
                            battery_level = ?
                            WHERE id = ?"""
            );

            statement.setTimestamp(1, Timestamp.from(scooterToStore.getUpdateTime()));
            statement.setObject(2, scooterToStore.getScooterType(), Types.OTHER);
            statement.setObject(3, scooterToStore.getStatus(), Types.OTHER);
            statement.setInt(4, scooterToStore.getBatteryLevel());
            statement.setObject(5, scooterToStore.getId(), Types.OTHER);
            statement.executeUpdate();

            return scooterToStore;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Scooter> findById(UUID id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM scooters WHERE id = ?;"
            );
            statement.setObject(1, id, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Scooter.createScooterFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Scooter> findAll() {
        List<Scooter> scooterList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM scooters;"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Scooter scooter = Scooter.createScooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            return scooterList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean delete(UUID id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            DELETE FROM scooters
                            WHERE id = ?"""
            );
            statement.setObject(1, id, Types.OTHER);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Scooter> findScootersByStatus(ScooterStatus scooterStatus) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Scooter> scooterList = new ArrayList<>();

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM scooters WHERE scooter_status = ?;"
            );
            statement.setObject(1, scooterStatus, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Scooter scooter = Scooter.createScooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            return scooterList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
