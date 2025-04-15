package org.riders.sharing.service;

import org.riders.sharing.model.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer getById(UUID id);

    Customer register(Customer customer);

    Customer login(String email, String password);

    Customer changePassword(UUID customerId, String oldPassword, String newPassword);
}
