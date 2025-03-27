package org.riders.sharing.service.impl;

import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.service.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOngoingOrder(UUID customerId) {
        Optional<Order> maybeOrder = orderRepository.findOngoingOrderByCustomer(customerId);
        return maybeOrder.orElseThrow(() -> new ElementNotFoundException(
                "Can't find active order for customer with id %s".formatted(customerId)));
    }

    public Order updateOrder(Order order) {
        if (orderRepository.isExists(order)) {
            return orderRepository.update(order);
        }
        throw new ElementNotFoundException("Attempt to update a non-existent order");
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllCustomerCompletedOrders(UUID id) {
        return orderRepository.findCustomerOrdersByStatus(id, OrderStatus.COMPLETED);
    }

    public Order getById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Couldn't find order with id %s"
                        .formatted(id)));
    }
}
