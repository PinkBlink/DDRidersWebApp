package org.riders.sharing.dto;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderDto(
    UUID id,
    UUID customerId,
    UUID scooterId,
    Instant startTime,
    Instant endTime,
    OrderStatus status
){
    public static OrderDto fromOrder(Order order){
        return new OrderDto(
            order.getId(),
            order.getCustomerId(),
            order.getScooter().getId(),
            order.getStartTime(),
            order.getEndTime(),
            order.getStatus()
        );
    }
}
