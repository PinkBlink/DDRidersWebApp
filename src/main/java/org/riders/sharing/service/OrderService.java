package org.riders.sharing.service;

import org.riders.sharing.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService extends BaseService<Order> {
    Order getOngoingOrder(UUID customerId);

    Order updateOrder(Order order);

    Order saveOrder(Order order);

    List<Order> getAllCompletedOrders(UUID customerId);
}
