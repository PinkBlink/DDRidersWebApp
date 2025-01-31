package org.riders.sharing.factory;

import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterType;

public interface ScooterFactory {
    Scooter createScooter(int id, ScooterType scooterType);
}
