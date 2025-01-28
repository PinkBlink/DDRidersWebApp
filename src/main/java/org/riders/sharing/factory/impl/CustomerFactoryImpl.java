package org.riders.sharing.factory.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.model.Customer;

public class CustomerFactoryImpl implements CustomerFactory {
    private final Logger logger = LogManager.getLogger(this);
    @Override
    public Customer createCustomer(int id, String name, String surname, String email, String passwordHash) {
        Customer customer = new Customer(id, name, surname, email, passwordHash);
        logger.info("Customer has been successfully created: "+ customer);
        return customer;
    }
}
