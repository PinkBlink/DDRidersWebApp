package org.riders.sharing.repository;

import org.riders.sharing.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository{
    //create
    void saveScooterRent(Order order);

    //update
    void updateScooterRent(Order order);

    //read
    Optional<Order> findScooterRentById(int id);
    List<Order> findAll();
    List<Order> findOngoingRents();
    List<Order> findCompletedRents();
    //delete
    void deleteScooterRent(Order order);
}
