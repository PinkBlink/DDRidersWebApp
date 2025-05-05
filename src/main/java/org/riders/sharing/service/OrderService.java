package org.riders.sharing.service;

import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.model.Order;

import java.util.UUID;

public interface OrderService {
    Order createOrder(CreateOrderDto createOrderDto);

    Order getById(UUID id);
}
