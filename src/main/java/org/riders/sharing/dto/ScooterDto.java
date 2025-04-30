package org.riders.sharing.dto;

import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.util.UUID;

public record ScooterDto(UUID id, ScooterType scooterType, ScooterStatus scooterStatus,
                         int batteryLevel) implements Pageable {
    public static ScooterDto fromScooter(Scooter scooter) {
        return new ScooterDto(scooter.getId(), scooter.getType(), scooter.getStatus(), scooter.getBatteryLevel());
    }
}
