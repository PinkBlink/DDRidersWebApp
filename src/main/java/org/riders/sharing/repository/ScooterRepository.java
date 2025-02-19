package org.riders.sharing.repository;

import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Scooter;

import java.util.List;

public interface ScooterRepository extends BaseRepository<Scooter> {
    List<Scooter> findAvailableScooters() throws ElementNotFoundException;
}
