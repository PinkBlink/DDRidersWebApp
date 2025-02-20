package org.riders.sharing.service.impl;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.CustomerRepository;
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
        scooterService.update(order.getScooter());
        return updatedOrder;
    }

    public Order createOrder(UUID customerId, UUID scooterId) {
        Scooter scooter = scooterService
                .update(
                        scooterService
                                .getById(scooterId)
                                .toBuilder()
                                .setStatus(ScooterStatus.RENTED)
                                .build()
                );

        return orderService.saveOrder(
                Order.Builder.getNewBuilder()
                        .setOrderStatus(OrderStatus.ONGOING)
                        .setScooter(scooter)
                        .setCustomerId(customerId)
                        .setStartTime(Instant.now())
                        .build());
    }
}
