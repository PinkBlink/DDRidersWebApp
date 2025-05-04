package org.riders.sharing.model;

import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Scooter extends BaseEntity {
    private final ScooterType type;
    private final ScooterStatus status;
    private final int batteryLevel;

    public Scooter(Scooter.Builder builder) {
        setId(builder.id);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);

        type = builder.type;
        status = builder.status;
        batteryLevel = builder.batteryLevel;
    }

    public ScooterStatus getStatus() {
        return status;
    }

    public ScooterType getType() {
        return type;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public Scooter.Builder toBuilder() {
        return new Scooter.Builder()
            .id(getId())
            .createTime(getCreateTime())
            .updateTime(getUpdateTime())
            .type(type)
            .status(status)
            .batteryLevel(batteryLevel);
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private ScooterType type;
        private ScooterStatus status;
        private int batteryLevel;

        public static Builder scooter() {
            return new Builder().id(UUID.randomUUID());
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Scooter.Builder createTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Scooter.Builder updateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Scooter.Builder type(ScooterType scooterType) {
            this.type = scooterType;
            return this;
        }

        public Scooter.Builder status(ScooterStatus status) {
            this.status = status;
            return this;
        }

        public Scooter.Builder batteryLevel(int batteryLevel) {
            this.batteryLevel = batteryLevel;
            return this;
        }

        public Scooter build() {
            return new Scooter(this);
        }
    }

    public static Scooter scooterFromResultSet(ResultSet resultSet) throws SQLException {
        final var id = UUID.fromString(resultSet.getString(1));
        final var createTime = resultSet.getTimestamp(2).toInstant();
        final var  updateTime = resultSet.getTimestamp(3).toInstant();
        final var scooterType = ScooterType.valueOf(resultSet.getString(4));
        final var scooterStatus = ScooterStatus.valueOf(resultSet.getString(5));
        final var batteryLevel = resultSet.getInt(6);

        return new Scooter.Builder()
            .id(id)
            .createTime(createTime)
            .updateTime(updateTime)
            .type(scooterType)
            .status(scooterStatus)
            .batteryLevel(batteryLevel)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Scooter scooter)) {
            return false;
        }

        return getId().equals(scooter.getId())
            && status == scooter.status
            && Objects.equals(type, scooter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), status, type);
    }

    @Override
    public String toString() {
        return "Scooter{" +
            "id=" + getId() +
            ", scooterType=" + type +
            ", status=" + status +
            ", batteryLevel=" + batteryLevel +
            '}';
    }
}
