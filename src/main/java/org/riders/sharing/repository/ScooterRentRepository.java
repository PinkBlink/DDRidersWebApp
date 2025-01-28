package org.riders.sharing.repository;

import org.riders.sharing.model.ScooterRent;

import java.util.List;
import java.util.Optional;

public interface ScooterRentRepository extends BaseRepository{
    //create
    void saveScooterRent(ScooterRent scooterRent);

    //update
    void updateScooterRent(ScooterRent scooterRent);

    //read
    Optional<ScooterRent> findScooterRentById(int id);
    List<ScooterRent> findAll();
    List<ScooterRent> findOngoingRents();
    List<ScooterRent> findCompletedRents();
    //delete
    void deleteScooterRent(ScooterRent scooterRent);
}
