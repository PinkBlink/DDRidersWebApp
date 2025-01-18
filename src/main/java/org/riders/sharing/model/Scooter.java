package org.riders.sharing.model;

import java.util.Objects;

public class Scooter {
    private int id;
    private boolean available;
    private ScooterRent scooterRent;
    private ScooterType scooterType;
    private int batteryLevel;

    public Scooter(int id, boolean available, ScooterRent scooterRent, ScooterType scooterType, int batteryLevel) {
        this.id = id;
        this.available = available;
        this.scooterRent = scooterRent;
        this.scooterType = scooterType;
        this.batteryLevel = batteryLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ScooterRent getScooterRent() {
        return scooterRent;
    }

    public void setScooterRent(ScooterRent scooterRent) {
        this.scooterRent = scooterRent;
    }

    public ScooterType getScooterType() {
        return scooterType;
    }

    public void setScooterType(ScooterType scooterType) {
        this.scooterType = scooterType;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scooter scooter)) {
            return false;
        }
        return id == scooter.id
                && available == scooter.available
                && Objects.equals(scooterRent, scooter.scooterRent)
                && Objects.equals(scooterType, scooter.scooterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, available, scooterRent, scooterType);
    }
}
