package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.dto.CustomerOrdersRequestDto;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.PaginationUtils;
import org.riders.sharing.utils.ValidationUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static org.riders.sharing.model.Order.Builder.order;
import static org.riders.sharing.model.enums.OrderStatus.COMPLETED;
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

    @Override
    public Order completeOrder(OrderDto orderDto) {
        ValidationUtils.checkThat(
            Objects.nonNull(orderDto.orderId()),
            () -> new BadRequestException("Order id is null.")
        );

        final var order = getById(orderDto.orderId());

        if (order.getStatus().equals(COMPLETED)) {
            LOGGER.error("Attempt to complete an already completed Order.");
            throw new IllegalStatusException("Order with id %s is already completed.".formatted(order.getId()));
        }

        final var releasedScooter = scooterService.releaseScooter(order.getScooter());
        final var completedOrder = orderRepository.update(
            order.toBuilder()
                .scooter(releasedScooter)
                .build()
                .complete()
        );

        LOGGER.info("Order with id {} has been successfully completed", order.getId());
        return completedOrder;
    }

    @Override
    public PageResponseDto<OrderDto> getCompletedCustomerOrders(CustomerOrdersRequestDto requestDto) {
        ValidationUtils.checkThat(
            Objects.nonNull(requestDto.customerId()),
            () -> new BadRequestException("Customer id is null.")
        );

        final var customerId = UUID.fromString(requestDto.customerId());
        final var pageRequest = requestDto.pageRequestDto();

        final var page = PaginationUtils.definePage(pageRequest.pageSize());
        final var pageSize = PaginationUtils.definePageSize(pageRequest.pageSize());
        final var offset = PaginationUtils.defineOffset(page, pageSize);

        final var completedOrders = orderRepository.findCompletedCustomerOrdersForResponse(
            customerId,
            pageSize,
            offset
        );

        final var completedOrdersDto = completedOrders
            .stream()
            .map(OrderDto::fromOrder)
            .toList();

        final var totalElements = orderRepository.getCompletedCustomerOrdersAmount(customerId);
        final var totalPages = PaginationUtils.calculateTotalPages(totalElements, pageSize);

        final var pageResponseDto = new PageResponseDto<>(
            completedOrdersDto,
            pageRequest.page(),
            pageRequest.pageSize(),
            totalElements,
            totalPages
        );

        LOGGER.info("Find {} completed orders for customer with id {}", totalElements, customerId);
        return pageResponseDto;
    }
}
