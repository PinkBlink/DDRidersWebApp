package org.riders.sharing.repository;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order> {
    List<Order> findCustomerOrdersByStatus(UUID customerId, OrderStatus status);

    List<Order> findOrdersByStatus(OrderStatus orderStatus);

    int getCompletedCustomerOrdersAmount(UUID customerId);

    List<Order> findCompletedCustomerOrdersForResponse(UUID customerId, int pageSize, int offset);
}
