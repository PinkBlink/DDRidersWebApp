package org.riders.sharing.dto;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public class OrderDTO {
    private UUID orderId;
    private UUID customerId;
    private ScooterDTO scooterDTO;
    private Instant startTime;
    private Instant endTime;
    private OrderStatus orderStatus;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public ScooterDTO getScooterDTO() {
        return scooterDTO;
    }

    public void setScooterDTO(ScooterDTO scooterDTO) {
        this.scooterDTO = scooterDTO;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static OrderDTO parse(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = order.getCustomerId();
        orderDTO.scooterDTO = ScooterDTO.parse(order.getScooter());
        orderDTO.startTime = order.getStartTime();
        orderDTO.endTime = order.getEndTime();
        orderDTO.orderStatus = order.getOrderStatus();
        return orderDTO;
    }
}
