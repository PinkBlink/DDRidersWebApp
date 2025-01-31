package org.riders.sharing.model;

import java.util.Objects;

public class Customer extends BaseEntity {
    private int customerId;
    private String name;
    private String surname;
    private String email;
    private String passwordHash;

    public Customer(int customerId, String name, String surname, String email, String passwordHash) {
        this.customerId = customerId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer customer)) {
            return false;
        }
        return customerId == customer.customerId
                && passwordHash.equals(customer.passwordHash)
                && Objects.equals(name, customer.name)
                && Objects.equals(surname, customer.surname)
                && Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, name, surname, email, passwordHash);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}