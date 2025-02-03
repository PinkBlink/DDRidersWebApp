package org.riders.sharing.service;

import org.riders.sharing.exception.ServiceException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Order;


public interface CustomerService{
    Customer register(Customer customer);
    Customer changePassword(Customer customer, String passwordHash) throws ServiceException;
    Customer logIn(String email, String password);
    Order showActiveOrder(Customer customer);
}
