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
        final var maybeCustomer = customerRepository.findByEmail(email);

        if (maybeCustomer.isPresent() && maybeCustomer.get().getPassword().equals(password)) {
            final var customer = maybeCustomer.get();
            logger.info("Customer login successfully: {}", customer);
            return customer;
        }

        logger.error("Bad attempt to login: {}", email);
        throw new InvalidCredentialsException("Wrong email or password!");
    }
}
