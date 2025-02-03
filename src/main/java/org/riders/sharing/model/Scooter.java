package org.riders.sharing.model;

import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.util.Objects;

public class Scooter {
    private int id;
    private ScooterType scooterType;
    private ScooterStatus status;
    private int batteryLevel;

    public Scooter(int id, ScooterType scooterType) {
        this.id = id;
        this.status = ScooterStatus.AVAILABLE;
        this.scooterType = scooterType;
        this.batteryLevel = 100;
    }

    public Scooter(int id, ScooterType scooterType, ScooterStatus status, int batteryLevel) {
        this.id = id;
        this.scooterType = scooterType;
        this.status = status;
        this.batteryLevel = batteryLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ScooterStatus getStatus() {
        return status;
    }

    public void setStatus(ScooterStatus status) {
        this.status = status;
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
                && status == scooter.status
                && Objects.equals(scooterType, scooter.scooterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, scooterType);
    }

    @Override
    public String toString() {
        return "Scooter{" +
                "id=" + id +
                ", scooterType=" + scooterType +
                ", status=" + status +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}