package org.riders.sharing.service;

import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;

import java.util.List;

public interface ScooterService extends BaseService<Scooter> {
    Scooter update(Scooter scooter);
    List<Scooter> getScooterListByStatus(ScooterStatus scooterStatus);
}
