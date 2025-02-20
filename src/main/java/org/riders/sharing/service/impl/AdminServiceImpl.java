package org.riders.sharing.service.impl;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.service.AdminService;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;

import java.time.Instant;
import java.util.UUID;

public class AdminServiceImpl implements AdminService {
    private final CustomerService customerService;
    private final ScooterService scooterService;
    private final OrderService orderService;

    public AdminServiceImpl(CustomerService customerRepository, ScooterService scooterService, OrderService orderService) {
        this.customerService = customerRepository;
        this.scooterService = scooterService;
        this.orderService = orderService;
    }

    @Override
    public Order realiseOrder(Order order) {
        Order updatedOrder = orderService.updateOrder(order.complete());
        scooterService.update(updatedOrder.getScooter());
        return updatedOrder;
    }

    public Order createOrder(UUID customerId, UUID scooterId) {
        Scooter scooter = scooterService.getById(scooterId);
        scooter = scooterService.update(scooter
                .toBuilder()
                .setStatus(ScooterStatus.RENTED)
                .build());
        Order order = Order.Builder.getNewBuilder()
                .setCustomerId(customerId)
                .setScooter(scooter)
                .setStartTime(Instant.now())
                .setOrderStatus(OrderStatus.ONGOING)
                .build();
        return orderService.saveOrder(order);
    }
}
