package org.riders.sharing.model;

import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        final var updatedScooter = scooter.toBuilder().status(ScooterStatus.AVAILABLE).build();

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
        final var orderId = UUID.fromString(resultSet.getString(1));
        final var orderCreateTime = resultSet.getTimestamp(2).toInstant();
        final var orderUpdateTime = resultSet.getTimestamp(3).toInstant();
        final var customerId = UUID.fromString(resultSet.getString(4));
        final var scooterId = UUID.fromString(resultSet.getString(5));
        final var startTime = resultSet.getTimestamp(6).toInstant();
        final var maybeEndTime = resultSet.getTimestamp(7);
        final var orderStatus = OrderStatus.valueOf(resultSet.getString(8));
        final var endTime = (maybeEndTime == null)
            ? null
            : maybeEndTime.toInstant();

        final var scooterCreateTime = resultSet.getTimestamp(10).toInstant();
        final var scooterUpdateTime = resultSet.getTimestamp(11).toInstant();
        final var scooterType = ScooterType.valueOf(resultSet.getString(12));
        final var scooterStatus = ScooterStatus.valueOf(resultSet.getString(13));
        final var batteryLevel = resultSet.getInt(14);

        final var scooter = new Scooter.Builder()
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
            && customerId.equals(order.customerId)
            && Objects.equals(scooter.getId(), order.scooter.getId())
            && Objects.equals(startTime.truncatedTo(ChronoUnit.MILLIS), order.startTime.truncatedTo(ChronoUnit.MILLIS))
            && Objects.equals((endTime != null)
                                    ? endTime.truncatedTo(ChronoUnit.MILLIS)
                                    : null,
                                (order.endTime != null)
                                    ? order.endTime.truncatedTo(ChronoUnit.MILLIS)
                                    : null)
            && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), customerId, scooter.getId(), startTime, endTime, status);
    }

    @Override
    public String toString() {
        return "Order{" +
            "orderId=" + getId() +
            ", customerId=" + customerId +
            ", scooterId=" + scooter.getId() +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", orderStatus=" + status +
            '}';
    }
}
