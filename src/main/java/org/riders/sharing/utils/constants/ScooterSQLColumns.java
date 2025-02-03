package org.riders.sharing.utils.constants;

public enum ScooterSQLColumns {
    TABLE_NAME("scooters"),
    SCOOTER_ID("scooter_id"),
    SCOOTER_TYPE("scooter_type"),
    SCOOTER_STATUS("scooter_status"),
    BATTERY_LEVEL("battery_level");
    private final String name;

    ScooterSQLColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}