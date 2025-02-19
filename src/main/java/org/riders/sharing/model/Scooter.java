package org.riders.sharing.model;

import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Scooter extends BaseEntity {
    private final ScooterType scooterType;
    private final ScooterStatus status;
    private final int batteryLevel;

    public Scooter(Scooter.Builder builder) {
        setId(builder.id);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        scooterType = builder.scooterType;
        status = builder.status;
        batteryLevel = builder.batteryLevel;
    }

    public ScooterStatus getStatus() {
        return status;
    }

    public ScooterType getScooterType() {
        return scooterType;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public Scooter.Builder toBuilder() {
        return new Scooter.Builder()
                .setId(getId())
                .setCreateTime(getCreateTime())
                .setUpdateTime(getUpdateTime())
                .setScooterType(scooterType)
                .setStatus(status)
                .setBatteryLevel(batteryLevel);
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private ScooterType scooterType;
        private ScooterStatus status;
        private int batteryLevel;

        public Scooter.Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public static Builder getNewBuilder() {
            return new Builder().setId(UUID.randomUUID());
        }

        public Scooter.Builder setCreateTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Scooter.Builder setUpdateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Scooter.Builder setScooterType(ScooterType scooterType) {
            this.scooterType = scooterType;
            return this;
        }

        public Scooter.Builder setStatus(ScooterStatus status) {
            this.status = status;
            return this;
        }

        public Scooter.Builder setBatteryLevel(int batteryLevel) {
            this.batteryLevel = batteryLevel;
            return this;
        }

        public Scooter build() {
            return new Scooter(this);
        }
    }

    public static Scooter createScooterFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(1));
        Instant createTime = resultSet.getTimestamp(2).toInstant();
        Instant updateTime = resultSet.getTimestamp(3).toInstant();
        ScooterType scooterType = ScooterType.valueOf(resultSet.getString(4));
        ScooterStatus scooterStatus = ScooterStatus.valueOf(resultSet.getString(5));
        int batteryLevel = resultSet.getInt(6);

        return new Scooter.Builder()
                .setId(id)
                .setCreateTime(createTime)
                .setUpdateTime(updateTime)
                .setScooterType(scooterType)
                .setStatus(scooterStatus)
                .setBatteryLevel(batteryLevel)
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
                && Objects.equals(scooterType, scooter.scooterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), status, scooterType);
    }

    @Override
    public String toString() {
        return "Scooter{" +
                "id=" + getId() +
                ", scooterType=" + scooterType +
                ", status=" + status +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}
