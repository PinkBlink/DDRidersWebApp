package org.riders.sharing.repository.impl;

import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.utils.ErrorMessages;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.riders.sharing.utils.SqlUtils.DUPLICATE_ENTRY_SQL_ERR_CODE;

public class ScooterRepositoryImpl implements ScooterRepository {
    private final ConnectionPool connectionPool;

    public ScooterRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Scooter save(Scooter scooter) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            INSERT INTO scooters(
                id,
                create_time,
                update_time,
                scooter_type,
                scooter_status,
                battery_level
            )
            VALUES( ?, ?, ?, ?, ?, ?);""")) {
            final var scooterToStore = scooter.toBuilder()
                .createTime(Instant.now())
                .updateTime(Instant.now())
                .build();

            preparedStatement.setObject(1, scooterToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(scooterToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(scooterToStore.getUpdateTime()));
            preparedStatement.setObject(4, scooterToStore.getType(), Types.OTHER);
            preparedStatement.setObject(5, scooterToStore.getStatus(), Types.OTHER);
            preparedStatement.setInt(6, scooterToStore.getBatteryLevel());

            preparedStatement.executeUpdate();

            return scooterToStore;
        } catch (SQLException e) {
            if (e.getSQLState().equals(DUPLICATE_ENTRY_SQL_ERR_CODE)) {
                throw new DuplicateEntryException(
                    ErrorMessages.SCOOTER_DUPLICATE.formatted(scooter.getId()), e);
            }

            throw new DatabaseException(
                "Error occurred when trying to save scooter with id %s".formatted(scooter.getId()), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Scooter update(Scooter scooter) {
        final var connection = connectionPool.getConnection();

        try (final var statement = connection.prepareStatement("""
            UPDATE scooters
            SET update_time = ?,
            scooter_type = ?,
            scooter_status = ?,
            battery_level = ?
            WHERE id = ?""")) {
            final var scooterToStore = scooter.toBuilder()
                .updateTime(Instant.now())
                .build();

            statement.setTimestamp(1, Timestamp.from(scooterToStore.getUpdateTime()));
            statement.setObject(2, scooterToStore.getType(), Types.OTHER);
            statement.setObject(3, scooterToStore.getStatus(), Types.OTHER);
            statement.setInt(4, scooterToStore.getBatteryLevel());
            statement.setObject(5, scooterToStore.getId(), Types.OTHER);

            statement.executeUpdate();

            return scooterToStore;
        } catch (SQLException e) {
            throw new DatabaseException(
                "Error occurred when trying to update scooter with id %s".formatted(scooter.getId()), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Scooter> findById(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var statement = connection.prepareStatement("SELECT * FROM scooters WHERE id = ?;")) {
            statement.setObject(1, id, Types.OTHER);
            final var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Scooter.scooterFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find scooter %s".formatted(id), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Scooter> findAll() {
        final var scooterList = new ArrayList<Scooter>();
        final var connection = connectionPool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM scooters;")) {
            final var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                final var scooter = Scooter.scooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            return scooterList;
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find all scooters", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var statement = connection.prepareStatement("""
            DELETE FROM scooters
            WHERE id = ?""")) {

            statement.setObject(1, id, Types.OTHER);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to delete scooter", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Scooter> findScootersByStatus(ScooterStatus scooterStatus) {
        final var connection = connectionPool.getConnection();
        final var scooterList = new ArrayList<Scooter>();

        try (final var statement = connection.prepareStatement(
            "SELECT * FROM scooters WHERE scooter_status = ?;")) {
            statement.setObject(1, scooterStatus, Types.OTHER);

            final var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                final var scooter = Scooter.scooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            return scooterList;
        } catch (SQLException e) {
            throw new DatabaseException(
                "Error occurred when trying to find scooters by status %s".formatted(scooterStatus), e
            );
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Scooter> findAvailableScootersForResponse(int limit, int offset) {
        final var connection = connectionPool.getConnection();
        final var scooterList = new ArrayList<Scooter>();

        try (final var statement = connection.prepareStatement("""
            SELECT * FROM scooters
            WHERE scooter_status = 'AVAILABLE'
            ORDER BY battery_level DESC
            LIMIT ?
            OFFSET ?;
            """)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            final var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                final var scooter = Scooter.scooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }

            return scooterList;
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find scooters for response", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public int getAvailableScootersAmount() {
        final var connection = connectionPool.getConnection();

        try (final var statement = connection.prepareStatement("""
                SELECT COUNT(*) FROM scooters
                WHERE scooter_status = 'AVAILABLE'
            """)) {
            final var resultSet = statement.executeQuery();

            resultSet.next();
            return resultSet.getInt(1);

        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to calculate available scooters", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
