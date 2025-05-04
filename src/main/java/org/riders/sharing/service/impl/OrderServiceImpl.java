package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.ValidationUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.riders.sharing.model.enums.OrderStatus.ONGOING;

public class OrderServiceImpl implements OrderService {
    private final CustomerService customerService;
    private final ScooterService scooterService;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(
        CustomerService customerService,
        ScooterService scooterService,
        OrderRepository orderRepository
    ) {
        this.customerService = customerService;
        this.scooterService = scooterService;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(CreateOrderDto createOrderDto) {
        ValidationUtils.checkThat(
            Objects.nonNull(createOrderDto.customerId())
                && Objects.nonNull(createOrderDto.scooterId()),
            () -> new BadRequestException("Scooter or customer id is null.")
        );

        final var customerId = UUID.fromString(createOrderDto.customerId());
        final var scooterId = UUID.fromString(createOrderDto.scooterId());


        final var customerFromDb = customerService.getById(customerId);
        final var scooterFromDb = scooterService.getById(scooterId);
        final var rentedScooterFromDb = scooterService.rentScooter(scooterFromDb);

        final var orderToStore = Order.Builder
            .order()
            .customerId(customerFromDb.getId())
            .status(ONGOING)
            .scooter(rentedScooterFromDb)
            .startTime(Instant.now())
            .build();

        return orderRepository.save(orderToStore);
    }
}
