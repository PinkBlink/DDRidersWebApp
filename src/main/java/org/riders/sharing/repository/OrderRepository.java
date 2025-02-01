package org.riders.sharing.repository;

import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository {
    //create
    void saveOrder(Order order) throws RepositoryException;

    //update
    void updateOrder(Order order);

    //read
    Optional<Order> findOrderById(int id) throws RepositoryException;

    List<Order> findAll() throws RepositoryException;

    List<Order> findOrdersByStatus(OrderStatus orderStatus) throws RepositoryException;

    //delete
    void deleteOrder(Order order) throws RepositoryException;
}