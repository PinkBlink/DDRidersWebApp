package org.riders.sharing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.util.UUID;

@JsonRootName("ScooterDTO")
public class ScooterDTO {
    @JsonProperty("scooterId")
    private UUID id;
    private ScooterType scooterType;
    private ScooterStatus scooterStatus;
    private int battery;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ScooterType getScooterType() {
        return scooterType;
    }

    public void setScooterType(ScooterType scooterType) {
        this.scooterType = scooterType;
    }

    public ScooterStatus getScooterStatus() {
        return scooterStatus;
    }

    public void setScooterStatus(ScooterStatus scooterStatus) {
        this.scooterStatus = scooterStatus;
    }

    public static ScooterDTO parse(Scooter scooter) {
        ScooterDTO scooterDTO = new ScooterDTO();
        scooterDTO.id = scooter.getId();
        scooterDTO.scooterType = scooter.getScooterType();
        scooterDTO.scooterStatus = scooter.getStatus();
        scooterDTO.battery = scooter.getBatteryLevel();
        return scooterDTO;
    }
}
