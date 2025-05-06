package org.riders.sharing.dto;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public record OrderDto(
    UUID orderId,
    UUID customerId,
    UUID scooterId,
    Instant startTime,
    Instant endTime,
    OrderStatus status
) implements Pageable {
    public static OrderDto fromOrder(Order order) {
        return new OrderDto(
            order.getId(),
            order.getCustomerId(),
            order.getScooter().getId(),
            order.getStartTime().truncatedTo(ChronoUnit.MILLIS),
            Optional.ofNullable(order.getEndTime())
                .map(time -> time.truncatedTo(ChronoUnit.MILLIS))
                .orElse(null),
            order.getStatus()
        );
    }
}
