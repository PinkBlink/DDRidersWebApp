package org.riders.sharing.service;

import org.riders.sharing.model.Customer;


public interface CustomerService extends BaseService<Customer> {
    Customer register(Customer customer);

    Customer changePassword(Customer customer, String oldPassword, String newPassword);

    Customer logIn(String email, String password);
}
