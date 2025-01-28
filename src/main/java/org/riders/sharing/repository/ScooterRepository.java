package org.riders.sharing.repository;

import org.riders.sharing.model.Scooter;

import java.util.List;
import java.util.Optional;

public interface ScooterRepository extends BaseRepository{
    //create
    void saveScooter(Scooter scooter);

    //update
    void updateScooter(Scooter scooter);

    //read
    Optional<Scooter> findScooterById(int id);

    List<Scooter> findAll();

    //delete
    void deleteCustomer(Scooter scooter);
}
