package org.riders.sharing.repository;

import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.model.Scooter;

import java.util.List;
import java.util.Optional;

public interface ScooterRepository extends BaseRepository {
    //create
    void saveScooter(Scooter scooter) throws RepositoryException;

    //update
    void updateScooter(Scooter scooter);

    //read
    Optional<Scooter> findScooterById(int id) throws RepositoryException;

    List<Scooter> findAll() throws RepositoryException;

    //delete
    void deleteScooter(Scooter scooter) throws RepositoryException;
}