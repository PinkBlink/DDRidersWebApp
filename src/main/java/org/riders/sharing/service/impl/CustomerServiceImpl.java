package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer login(String email, String password) {
        if (email == null || password == null) {
            logger.error("Password or Email is null.");
            throw new InvalidCredentialsException("Password or Email is null.");
        }

        final var customer = customerRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("Bad attempt to login: {}", email);
                return new InvalidCredentialsException("Wrong email or password!");
            });

        if (!customer.getPassword().equals(password)) {
            logger.error("Wrong email or password!");
            throw new InvalidCredentialsException("Wrong email or password!");
        }

        logger.info("Customer {} login successfully", customer.getEmail());
        return customer;
    }
}
