package org.riders.sharing.utils.constants;

public class ScooterSqlQueries {
    public static final String INSERT_SCOOTER =
            "INSERT INTO scooters(scooter_id, type, status, battery_level)" +
                    "VALUES(?, ?, ?, ?);";
    public static final String UPDATE_SCOOTER =
            "UPDATE scooters" +
                    "SET type = ?" +
                    "SET status = ?" +
                    "SET battery_level = ?" +
                    "WHERE scooter_id = ?;";
}
