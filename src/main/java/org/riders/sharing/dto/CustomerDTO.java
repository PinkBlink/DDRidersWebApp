package org.riders.sharing.dto;

import org.riders.sharing.model.Customer;

import java.util.Objects;

public class CustomerDTO {
    private String name;
    private String surname;
    private String email;

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

    public static CustomerDTO parse(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.name = customer.getName();
        customerDTO.surname = customer.getSurname();
        customerDTO.email = customer.getEmail();
        return customerDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerDTO that)) {
            return false;
        }
        return Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email);
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
