package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.BadDatabaseSelectException;
import org.riders.sharing.exception.BadDatabaseUpdateException;
import org.riders.sharing.exception.DuplicateIdException;
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
    private final Logger logger = LogManager.getLogger(this);
    private final ConnectionPool connectionPool = ConnectionPool.INSTANCE;

    @Override
    public Scooter save(Scooter scooter) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Scooter scooterToStore = scooter.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement("""
                    INSERT INTO scooters(id, create_time, update_time, scooter_type, scooter_status, battery_level)
                    VALUES( ?, ?, ?, ?, ?, ?);
                    """);


            preparedStatement.setObject(1, scooterToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(scooterToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(scooterToStore.getUpdateTime()));
            preparedStatement.setObject(4, scooterToStore.getScooterType(), Types.OTHER);
            preparedStatement.setObject(5, scooterToStore.getStatus(), Types.OTHER);
            preparedStatement.setInt(6, scooterToStore.getBatteryLevel());

            preparedStatement.executeUpdate();

            logger.info("Scooter {} has been successfully saved", scooter);

            return scooterToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to save scooter: {}", scooter, e);
            throw new DuplicateIdException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(preparedStatement);
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

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                logger.info("Scooter has been successfully updated {}", scooter);
            } else {
                logger.info("Couldn't find scooter: {}", scooter);
            }

            return scooterToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to update scooter: {}", scooter, e);
            throw new BadDatabaseUpdateException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Scooter> find(UUID id) {
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
                logger.info("Successfully found scooter with Id: {}", id);
                return Optional.of(Scooter.createScooterFromResultSet(resultSet));
            }

            logger.info("Couldn't find scooter with id: {}", id);

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find scooter with id :{}", id);
            throw new BadDatabaseSelectException(e.getMessage(), e);
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

            if (scooterList.isEmpty()) {
                logger.warn("Scooter list is empty");
            } else {
                logger.info("Found {} scooters;", scooterList.size());
            }

            return scooterList;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find all scooters;");
            throw new BadDatabaseSelectException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean isExist(Scooter scooter) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement("""
                    SELECT * FROM scooters
                    WHERE id = ?"""
            );

            statement.setObject(1, scooter.getId(), Types.OTHER);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to check the existing scooter with id {}"
                    , scooter.getId());
            throw new BadDatabaseSelectException(e.getMessage(), e);
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

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                logger.info("Scooter with id {} has been successfully deleted", id);
            } else {
                logger.info("Couldn't find scooter with id: {}", id);
            }

            return success;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to delete scooter with id: {}", id);
            throw new BadDatabaseUpdateException(e.getMessage(), e);
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

            logger.info("Found {} scooters with status {}", scooterList.size(), scooterStatus);

            return scooterList;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find scooters with status {}", scooterStatus, e);
            throw new BadDatabaseSelectException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
