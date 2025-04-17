package org.riders.sharing.service;

import org.riders.sharing.model.Customer;

public interface CustomerService {
    Customer login(String email, String password);

    Customer register(Customer customer);
}
