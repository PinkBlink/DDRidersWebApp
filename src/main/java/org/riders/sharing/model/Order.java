package org.riders.sharing.model;

import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Order extends BaseEntity {
    private final UUID customerId;
    private final Scooter scooter;
    private final Instant startTime;
    private final Instant endTime;
    private final OrderStatus orderStatus;

    public Order(Order.Builder builder) {
        setId(builder.id);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        customerId = builder.customerId;
        scooter = builder.scooter;
        startTime = builder.startTime;
        endTime = builder.endTime;
        orderStatus = builder.orderStatus;

    }

    public UUID getCustomerId() {
        return customerId;
    }


    public Scooter getScooter() {
        return scooter;
    }


    public Instant getStartTime() {
        return startTime;
    }


    public Instant getEndTime() {
        return endTime;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }


    public Order.Builder toBuilder() {
        return new Builder()
                .setId(getId())
                .setCreateTime(getCreateTime())
                .setUpdateTime(getUpdateTime())
                .setScooter(scooter)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setOrderStatus(orderStatus);
    }

    public Order complete() {
        this.toBuilder().setOrderStatus(OrderStatus.COMPLETED).build();
        this.toBuilder().setEndTime(Instant.now()).build();
        return this;
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private UUID customerId;
        private Scooter scooter;
        private Instant startTime;
        private Instant endTime;
        private OrderStatus orderStatus;

        public Order.Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public static Builder getNewBuilder(){
            return new Builder().setId(UUID.randomUUID());
        }

        public Order.Builder setCreateTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Order.Builder setUpdateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }


        public Order.Builder setCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Order.Builder setScooter(Scooter scooter) {
            this.scooter = scooter;
            return this;
        }

        public Order.Builder setStartTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Order.Builder setEndTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Order.Builder setOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    public static Order createOrderFromResultSet(ResultSet resultSet) throws SQLException {

        UUID orderId = UUID.fromString(resultSet.getString(1));
        Instant orderCreateTime = resultSet.getTimestamp(2).toInstant();
        Instant orderUpdateTime = resultSet.getTimestamp(3).toInstant();
        UUID customerId = UUID.fromString(resultSet.getString(4));
        UUID scooterId = UUID.fromString(resultSet.getString(5));
        Instant startTime = resultSet.getTimestamp(6).toInstant();
        Timestamp maybeEndTime = resultSet.getTimestamp(7);
        OrderStatus orderStatus = OrderStatus.valueOf(resultSet.getString(8));
        Instant endTime = (maybeEndTime == null)
                ? null
                : resultSet.getTimestamp(7).toInstant();


        Instant scooterCreateTime = resultSet.getTimestamp(10).toInstant();
        Instant scooterUpdateTime = resultSet.getTimestamp(11).toInstant();
        ScooterType scooterType = ScooterType.valueOf(resultSet.getString(12));
        ScooterStatus scooterStatus = ScooterStatus.valueOf(resultSet.getString(13));
        int batteryLevel = resultSet.getInt(14);

        Scooter scooter = new Scooter.Builder()
                .setId(scooterId)
                .setCreateTime(scooterCreateTime)
                .setUpdateTime(scooterUpdateTime)
                .setScooterType(scooterType)
                .setStatus(scooterStatus)
                .setBatteryLevel(batteryLevel)
                .build();

        return new Order.Builder()
                .setId(orderId)
                .setCreateTime(orderCreateTime)
                .setUpdateTime(orderUpdateTime)
                .setCustomerId(customerId)
                .setScooter(scooter)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setOrderStatus(orderStatus)
                .build();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order order)) {
            return false;
        }
        return getId().equals(order.getId())
                && customerId == order.customerId
                && Objects.equals(scooter, order.scooter)
                && Objects.equals(startTime, order.startTime)
                && Objects.equals(endTime, order.endTime)
                && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), customerId, scooter, startTime, endTime, orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + getId() +
                ", customerId=" + customerId +
                ", scooter=" + scooter +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", orderStatus=" + orderStatus +
                '}';
    }
}