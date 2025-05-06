package org.riders.sharing.service;

import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.dto.CustomerOrdersRequestDto;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.model.Order;

import java.util.UUID;

public interface OrderService {
    Order createOrder(CreateOrderDto createOrderDto);

    Order getById(UUID id);

    Order completeOrder(OrderDto orderDto);

    PageResponseDto<OrderDto> getCompletedCustomerOrders(CustomerOrdersRequestDto requestDto);
}
