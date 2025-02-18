package org.riders.sharing.model;

import org.riders.sharing.utils.PasswordEncrypt;
import org.riders.sharing.utils.constants.CustomerSQLColumns;

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
                .setId(getId())
                .setCreateTime(getCreateTime())
                .setUpdateTime(getUpdateTime())
                .setName(name)
                .setSurname(surname)
                .setEmail(email)
                .setPassword(password);
    }

    public static class Builder {
        private UUID id;
        private Instant createTime;
        private Instant updateTime;
        private String name;
        private String surname;
        private String email;
        private String password;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setCreateTime(Instant createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setUpdateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = PasswordEncrypt.hashPassword(password);
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    public static Customer createCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(CustomerSQLColumns.ID.getName()));
        Instant createTime = resultSet.getTimestamp(CustomerSQLColumns.CREATE_TIME.getName()).toInstant();
        Instant updateTime = resultSet.getTimestamp(CustomerSQLColumns.CREATE_TIME.getName()).toInstant();
        String name = resultSet.getString(CustomerSQLColumns.NAME.getName());
        String surname = resultSet.getString(CustomerSQLColumns.SURNAME.getName());
        String email = resultSet.getString(CustomerSQLColumns.EMAIL.getName());
        String password = resultSet.getString(CustomerSQLColumns.PASSWORD_HASH.getName());

        return new Customer.Builder()
                .setId(id)
                .setCreateTime(createTime)
                .setUpdateTime(updateTime)
                .setName(name)
                .setSurname(surname)
                .setEmail(email)
                .setPassword(password)
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
