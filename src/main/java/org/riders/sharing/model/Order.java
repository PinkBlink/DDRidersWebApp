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
    private final OrderStatus status;

    public Order(Order.Builder builder) {
        setId(builder.id);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        customerId = builder.customerId;
        scooter = builder.scooter;
        startTime = builder.startTime;
        endTime = builder.endTime;
        status = builder.status;
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

    public OrderStatus getStatus() {
        return status;
    }


    public Order.Builder toBuilder() {
        return new Builder()
                .id(getId())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .customerId(getCustomerId())
                .scooter(scooter)
                .startTime(startTime)
                .endTime(endTime)
                .status(status);
    }

    public Order complete() {
        Scooter updatedScooter = scooter.toBuilder().status(ScooterStatus.AVAILABLE).build();

        return this.toBuilder()
                .status(OrderStatus.COMPLETED)
                .endTime(Instant.now())
                .scooter(updatedScooter)
                .build();
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private UUID customerId;
        private Scooter scooter;
        private Instant startTime;
        private Instant endTime;
        private OrderStatus status;

        public Order.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public static Builder order() {
            return new Builder().id(UUID.randomUUID());
        }

        public Order.Builder createTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Order.Builder updateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }


        public Order.Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Order.Builder scooter(Scooter scooter) {
            this.scooter = scooter;
            return this;
        }

        public Order.Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Order.Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Order.Builder status(OrderStatus orderStatus) {
            this.status = orderStatus;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    public static Order orderFromResultSet(ResultSet resultSet) throws SQLException {
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
                : maybeEndTime.toInstant();


        Instant scooterCreateTime = resultSet.getTimestamp(10).toInstant();
        Instant scooterUpdateTime = resultSet.getTimestamp(11).toInstant();
        ScooterType scooterType = ScooterType.valueOf(resultSet.getString(12));
        ScooterStatus scooterStatus = ScooterStatus.valueOf(resultSet.getString(13));
        int batteryLevel = resultSet.getInt(14);

        Scooter scooter = new Scooter.Builder()
                .id(scooterId)
                .createTime(scooterCreateTime)
                .updateTime(scooterUpdateTime)
                .type(scooterType)
                .status(scooterStatus)
                .batteryLevel(batteryLevel)
                .build();

        return new Order.Builder()
                .id(orderId)
                .createTime(orderCreateTime)
                .updateTime(orderUpdateTime)
                .customerId(customerId)
                .scooter(scooter)
                .startTime(startTime)
                .endTime(endTime)
                .status(orderStatus)
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
                && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), customerId, scooter, startTime, endTime, status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + getId() +
                ", customerId=" + customerId +
                ", scooter=" + scooter +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", orderStatus=" + status +
                '}';
    }
}