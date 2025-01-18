package org.riders.sharing.model;

import java.util.Objects;

public class Customer extends BaseEntity {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String passwordHash;

    private ScooterRent scooterRent;

    public Customer(int id, String name, String surname, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ScooterRent getScooterRent() {
        return scooterRent;
    }

    public void setScooterRent(ScooterRent scooterRent) {
        this.scooterRent = scooterRent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer customer)) {
            return false;
        }
        return id == customer.id
                && passwordHash.equals(customer.passwordHash)
                && Objects.equals(name, customer.name)
                && Objects.equals(surname, customer.surname)
                && Objects.equals(email, customer.email)
                && Objects.equals(scooterRent, customer.scooterRent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, passwordHash, scooterRent);
    }
}
