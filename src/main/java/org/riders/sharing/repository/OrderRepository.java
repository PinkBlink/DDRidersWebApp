package org.riders.sharing.repository;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order> {
    Optional<Order> findOngoingOrderByCustomer(UUID customerId);

    List<Order> findCustomerOrdersByStatus(UUID customerId, OrderStatus status);

    List<Order> findOrdersByStatus(OrderStatus orderStatus);
}
