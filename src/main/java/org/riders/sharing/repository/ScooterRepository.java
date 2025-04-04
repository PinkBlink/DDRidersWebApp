package org.riders.sharing.repository;

import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;

import java.util.List;

public interface ScooterRepository extends BaseRepository<Scooter> {
    List<Scooter> findScootersByStatus(ScooterStatus scooterStatus);
}
