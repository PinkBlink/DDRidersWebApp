package org.riders.sharing.factory;

import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ScooterFactory {
    Scooter createScooter(int id, ScooterType scooterType);
    Scooter createScooter(int id, ScooterType scooterType, ScooterStatus scooterStatus, int batteryLevel);

    Scooter createScooterFromResultSet(ResultSet resultSet) throws SQLException;
}
