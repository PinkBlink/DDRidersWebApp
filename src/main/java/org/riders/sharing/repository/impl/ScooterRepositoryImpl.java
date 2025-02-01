package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPull;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.ScooterFactory;
import org.riders.sharing.factory.impl.ScooterFactoryImpl;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.utils.constants.ScooterSqlQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ScooterRepositoryImpl implements ScooterRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final ConnectionPull connectionPull = ConnectionPull.getInstance();
    private final ScooterFactory scooterFactory = new ScooterFactoryImpl();

    @Override
    public void saveScooter(Scooter scooter) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(ScooterSqlQueries.INSERT_SCOOTER);

            int id = scooter.getId();
            Object type = scooter.getScooterType();
            Object status = scooter.getStatus();
            int batteryLevel = scooter.getBatteryLevel();

            statement.setInt(1, id);
            statement.setObject(2, type, Types.OTHER);
            statement.setObject(3, status, Types.OTHER);
            statement.setInt(4, batteryLevel);

            boolean isSuccessfully = statement.executeUpdate() == 1;
            if (isSuccessfully) {
                logger.info("Scooter: " + scooter + " successfully saved");
            } else {
                logger.info("Couldn't save scooter: " + scooter);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to save scooter: " + scooter, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void updateScooter(Scooter scooter) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(ScooterSqlQueries.UPDATE_SCOOTER);

            int scooterId = scooter.getId();
            String type = scooter.getScooterType().toString();
            String status = scooter.getStatus().toString();
            int batteryLevel = scooter.getBatteryLevel();

            statement.setString(1, type);
            statement.setString(2, status);
            statement.setInt(3, batteryLevel);
            statement.setInt(4, scooterId);

            if (statement.executeUpdate() > 0) {
                logger.info("Successfully update scooter: " + scooter);
            } else {
                logger.info("Couldn't update scooter: " + scooter);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to update scooter: " + scooter, e);
            throw new RuntimeException(e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Scooter> findScooterById(int id) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(ScooterSqlQueries.FIND_SCOOTER_BY_ID);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                logger.info("Successfully found scooter with Id: " + id);
                return Optional.of(scooterFactory.createScooterFromResultSet(resultSet));
            }
            logger.info("Couldn't find scooter with id: " + id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error occurred while trying to find scooter with id :" + id);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Scooter> findAll() throws RepositoryException {
        List<Scooter> scooterList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(ScooterSqlQueries.FIND_ALL_SCOOTERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Scooter scooter = scooterFactory.createScooterFromResultSet(resultSet);
                scooterList.add(scooter);
            }
            return scooterList;
        } catch (SQLException e) {
            logger.error("Error occurred while trying to find all scooters;");
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void deleteScooter(Scooter scooter) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        int scooterId = scooter.getId();
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(ScooterSqlQueries.DELETE_SCOOTER);
            statement.setInt(1, scooterId);
            int result = statement.executeUpdate();
            if (result > 0) {
                logger.info("Successfully delete scooter with id: " + scooterId);
            } else {
                logger.info("Couldn't find scooter with id: " + scooterId);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to delete scooter with id: " + scooterId);
            throw new RepositoryException(e.getMessage(), e);
        }
    }
}
