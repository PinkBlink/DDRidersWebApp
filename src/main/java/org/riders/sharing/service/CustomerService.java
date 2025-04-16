package org.riders.sharing.service;

import org.riders.sharing.model.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer login(String email, String password);
}
