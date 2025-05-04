package org.riders.sharing.service;

import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.model.Order;

public interface OrderService {
    Order createOrder(CreateOrderDto createOrderDto);
}
