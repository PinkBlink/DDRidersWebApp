package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.ValidationUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.riders.sharing.model.Order.Builder.order;
import static org.riders.sharing.model.enums.OrderStatus.ONGOING;

public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LogManager.getLogger(OrderServiceImpl.class);

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

        final var savedOrder = orderRepository.save(
            order()
                .customerId(customerFromDb.getId())
                .scooter(rentedScooterFromDb)
                .startTime(Instant.now())
                .status(ONGOING)
                .build()
        );

        LOGGER.info("Order {} was successfully saved", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public Order getById(UUID id) {
        final var maybeOrder = orderRepository.findById(id);

        return maybeOrder.orElseThrow(() -> {
                LOGGER.error("Couldn't find order with id: {}", id);
                return new NoElementException("Couldn't find order with id: %s".formatted(id));
            }
        );
    }
}
