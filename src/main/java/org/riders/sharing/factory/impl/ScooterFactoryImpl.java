package org.riders.sharing.factory.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.factory.ScooterFactory;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.utils.constants.ScooterSQLColumns;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScooterFactoryImpl implements ScooterFactory {
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public Scooter createScooter(int id, ScooterType scooterType) {
        Scooter scooter = new Scooter(id, scooterType);
        logger.info("Scooter has been successfully created " + scooter);
        return scooter;
    }

    @Override
    public Scooter createScooter(int id, ScooterType scooterType, ScooterStatus scooterStatus, int batteryLevel) {
        return new Scooter(id, scooterType, scooterStatus, batteryLevel);
    }

    public Scooter createScooterFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(ScooterSQLColumns.SCOOTER_ID.getName());
        ScooterType scooterType = ScooterType.valueOf(
                resultSet.getString(ScooterSQLColumns.SCOOTER_TYPE.getName()));
        ScooterStatus status = ScooterStatus.valueOf(
                resultSet.getString(ScooterSQLColumns.SCOOTER_STATUS.getName()));
        int batteryLevel = resultSet.getInt(ScooterSQLColumns.BATTERY_LEVEL.getName());

        return createScooter(id, scooterType, status, batteryLevel);
    }
}