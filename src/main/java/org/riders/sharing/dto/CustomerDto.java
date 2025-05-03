package org.riders.sharing.dto;

import org.riders.sharing.model.Customer;

import java.util.UUID;

public record CustomerDto(
    UUID id,
    String name,
    String surname,
    String email
) implements Pageable {
    public static CustomerDto fromCustomer(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getName(), customer.getSurname(), customer.getEmail());
    }
}
