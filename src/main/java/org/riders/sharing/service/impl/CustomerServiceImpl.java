package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.exception.UserExistsException;
import org.riders.sharing.exception.WrongEmailOrPasswordException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.PasswordEncryptor;

import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final Logger logger;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.logger = LogManager.getLogger(this);
    }

    @Override
    public Customer register(Customer customer) {
        String email = customer.getEmail();
        if (customerRepository.isExists(customer)) {
            String errMessage = "Customer with email %s is already exist".formatted(email);
            logger.error(errMessage);
            throw new UserExistsException(errMessage);
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer changePassword(Customer customer, String oldPassword, String newPassword) {
        String hashedOldPassword = PasswordEncryptor.hashPassword(oldPassword);

        if (customer.getPassword().equals(hashedOldPassword)) {
            return customerRepository.update(
                    customer.toBuilder()
                            .setPassword(PasswordEncryptor.hashPassword(newPassword))
                            .build()
            );
        }

        throw new WrongEmailOrPasswordException("Wrong email or password;");
    }

    @Override
    public Customer logIn(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UserExistsException("Wrong email or password;"));
        if (!customer.getPassword().equals(password)) {
            throw new WrongEmailOrPasswordException("Wrong email or password;");
        }
        return customer;
    }

    public Customer getById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Couldn't find customer with id %s"
                        .formatted(id)));
    }
}
