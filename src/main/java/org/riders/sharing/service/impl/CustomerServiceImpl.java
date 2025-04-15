package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;

import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getById(UUID id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Couldn't find customer with id {}", id);
                return new NoElementException("Couldn't find customer with id %s".formatted(id));
            });
    }

    @Override
    public Customer register(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer login(String email, String password) {
        final var maybeCustomer = customerRepository.findByEmail(email);

        if (maybeCustomer.isPresent() && maybeCustomer.get().getPassword().equals(password)) {
            return maybeCustomer.get();
        }

        logger.error("Bad attempt to login: {}", email);
        throw new InvalidCredentialsException("Wrong email or password!");
    }

    @Override
    public Customer changePassword(UUID id, String oldPassword, String newPassword) {
        final var customer = getById(id);

        if (customer.getPassword().equals(oldPassword)) {
            final var updatedCustomer = customer.toBuilder()
                .password(newPassword).build();
            logger.info("Password was successfully changed for customer with id {}", id);
            return customerRepository.update(updatedCustomer);
        }

        logger.error("Wrong attempt to change password for customer with id {}", id);
        throw new InvalidCredentialsException("There is no access for customer with id: %s".formatted(id));
    }
}
