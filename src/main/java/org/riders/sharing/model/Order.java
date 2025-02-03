package org.riders.sharing.model;

import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {
    private int orderId;
    private int customerId;
    private Scooter scooter;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private OrderStatus orderStatus;

    public Order(int orderId, int customerId, Scooter scooter, LocalDateTime startTime
            , LocalDateTime endTime, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.scooter = scooter;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderStatus = orderStatus;
    }

    public Order(int orderId, int customerId, Scooter scooter, LocalDateTime startTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.scooter = scooter;
        this.startTime = startTime;
        this.orderStatus = OrderStatus.ONGOING;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Scooter getScooterId() {
        return scooter;
    }

    public void setScooterId(Scooter scooter) {
        this.scooter = scooter;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void complete() {
        this.orderStatus = OrderStatus.COMPLETED;
        scooter.setStatus(ScooterStatus.AVAILABLE);
        endTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order order)) {
            return false;
        }
        return orderId == order.orderId
                && customerId == order.customerId
                && Objects.equals(scooter, order.scooter)
                && Objects.equals(startTime, order.startTime)
                && Objects.equals(endTime, order.endTime)
                && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, scooter, startTime, endTime, orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", scooter=" + scooter +
                ", scooter=" + scooter +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", orderStatus=" + orderStatus +
                '}';
    }
}