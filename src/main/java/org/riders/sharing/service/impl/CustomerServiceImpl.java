package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.exception.ServiceException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private Logger logger;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.logger = LogManager.getLogger(this);
    }

    @Override
    public Customer register(Customer customer) {
        return null;
    }

    @Override
    public Customer changePassword(Customer customer, String passwordHash) throws ServiceException {
        try {
            customer.setPasswordHash(passwordHash);
            customerRepository.updateCustomer(customer);
            logger.info("The password is successfully changed by the customer with ID: " + customer.getCustomerId());
            return customer;
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Customer logIn(String email, String password) {
        return null;
    }

    @Override
    public Order showActiveOrder(Customer customer) {
        return null;
    }
}
