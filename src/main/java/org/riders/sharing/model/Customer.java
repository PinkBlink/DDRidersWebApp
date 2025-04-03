package org.riders.sharing.model;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Customer extends BaseEntity {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    public Customer(Builder builder) {
        setId(builder.id);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        this.name = builder.name;
        this.surname = builder.surname;
        this.email = builder.email;
        this.password = builder.password;
    }

    public String getName() {
        return name;
    }


    public String getSurname() {
        return surname;
    }


    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }

    public Customer.Builder toBuilder() {
        return new Builder()
                .id(getId())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .name(name)
                .surname(surname)
                .email(email)
                .password(password);
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private String name;
        private String surname;
        private String email;
        private String password;

        public static Builder customer() {
            return new Builder().id(UUID.randomUUID());
        }


        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder createTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder updateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    public static Customer customerFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(1));
        Instant createTime = resultSet.getTimestamp(2).toInstant();
        Instant updateTime = resultSet.getTimestamp(3).toInstant();
        String name = resultSet.getString(4);
        String surname = resultSet.getString(5);
        String email = resultSet.getString(6);
        String password = resultSet.getString(7);

        return new Customer.Builder()
                .id(id)
                .createTime(createTime)
                .updateTime(updateTime)
                .name(name)
                .surname(surname)
                .email(email)
                .password(password)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Customer customer)) {
            return false;
        }

        return getId().equals(customer.getId())
                && password.equals(customer.password)
                && Objects.equals(name, customer.name)
                && Objects.equals(surname, customer.surname)
                && Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, surname, email, password);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
