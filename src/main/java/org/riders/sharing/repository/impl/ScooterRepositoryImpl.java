package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.ElementNotFoundException;
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
        PreparedStatement statement = null;

        try {
            Scooter scooterToStore = scooter
                    .toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            INSERT INTO scooters(id, create_time, update_time, scooter_type, scooter_status, battery_level)
                            VALUES(?, ?, ?, ?, ?, ?)"""
            );

            statement.setObject(1, scooterToStore.getId(), Types.OTHER);
            statement.setTimestamp(2, Timestamp.from(scooterToStore.getCreateTime()));
            statement.setTimestamp(3, Timestamp.from(scooterToStore.getUpdateTime()));
            statement.setObject(4, scooterToStore.getScooterType(), Types.OTHER);
            statement.setObject(5, scooterToStore.getStatus(), Types.OTHER);
            statement.setInt(6, scooterToStore.getBatteryLevel());

            boolean isSuccessfully = statement.executeUpdate() == 1;

            if (isSuccessfully) {
                logger.info("Scooter: " + scooter + " successfully saved");
            } else {
                logger.info("Couldn't save scooter: " + scooter);
            }

            return scooterToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to save scooter: " + scooter, e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
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
                            battery_level = ?,
                            WHERE scooter_id = ?"""
            );

            statement.setTimestamp(1, Timestamp.from(scooterToStore.getUpdateTime()));
            statement.setObject(2, scooterToStore.getScooterType(), Types.OTHER);
            statement.setObject(3, scooterToStore.getStatus(), Types.OTHER);
            statement.setInt(4, scooterToStore.getBatteryLevel());
            statement.setObject(5, scooterToStore.getId(), Types.OTHER);

            if (statement.executeUpdate() > 0) {
                logger.info("Successfully update scooter: " + scooter);
            } else {
                logger.info("Couldn't update scooter: " + scooter);
            }

            return scooter;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to update scooter: " + scooter, e);
            throw new ElementNotFoundException(e.getMessage(), e);
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
                logger.info("Successfully found scooter with Id: " + id);
                return Optional.of(Scooter.createScooterFromResultSet(resultSet));
            }

            logger.info("Couldn't find scooter with id: " + id);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find scooter with id :" + id);
            throw new ElementNotFoundException(e.getMessage(), e);
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

            logger.info("Find " + scooterList.size() + "scooters;");
            return scooterList;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find all scooters;");
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Scooter> findScootersByStatus(ScooterStatus scooterStatus) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM scooters WHERE scooter_status = ?;"
            );

            List<Scooter> scooterList = new ArrayList<>();
            statement.setObject(1, scooterStatus, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Scooter scooter = Scooter.createScooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            logger.info("Found " + scooterList.size() + " available scooters");
            return scooterList;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find available scooters", e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean isExists(Scooter scooter) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement("""
                    SELECT * FROM scooters
                    WHERE id = ?""");

            statement.setObject(1, scooter.getId(), Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to check the existing scooter with id %s"
                    .formatted(scooter.getId()));
            throw new RuntimeException(e);
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
                logger.info("Successfully delete scooter with id: " + id);
            } else {
                logger.info("Couldn't find scooter with id: " + id);
            }

            return success;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to delete scooter with id: %s".formatted(id));
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
