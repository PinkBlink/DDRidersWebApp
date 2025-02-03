package org.riders.sharing.utils.constants;

public class ScooterSQLQueries {
    public static final String INSERT_SCOOTER =
            "INSERT INTO scooters(scooter_id, scooter_type, scooter_status, battery_level)" +
                    " VALUES(?, ?, ?, ?);";
    public static final String UPDATE_SCOOTER =
            "UPDATE scooters " +
                    "SET scooter_type = ?, " +
                    "scooter_status = ?, " +
                    "battery_level = ?, " +
                    "WHERE scooter_id = ?;";
    public static final String FIND_ALL_SCOOTERS =
            "SELECT * FROM scooters ";
    public static final String FIND_SCOOTER_BY_ID =
            FIND_ALL_SCOOTERS +
                    "WHERE scooter_id = ?;";
    public static final String FIND_AVAILABLE_SCOOTERS =
            FIND_ALL_SCOOTERS +
                    "WHERE scooter_status = 'AVAILABLE'";

    public static final String DELETE_SCOOTER =
            "DELETE FROM scooters" +
                    "WHERE scooter_id = ?;";
}

