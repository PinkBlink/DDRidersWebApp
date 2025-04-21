package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.LoginDTO;
import org.riders.sharing.dto.RegistrationDTO;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.utils.ValidationUtils;

import java.util.Objects;

import static org.riders.sharing.model.Customer.Builder.customer;

public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer login(LoginDTO loginDTO) {
        final var email = loginDTO.email();
        final var password = loginDTO.password();

        ValidationUtils.checkThat(Objects.nonNull(email) && Objects.nonNull(password)
            , () -> new BadRequestException("Email or Password is null."));

        final var customer = customerRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Bad attempt to login: {}", email);
            return new UnauthorizedException("Wrong email or password!");
        });

        final var encryptedPassword = PasswordEncryptor.encryptPassword(password);

        if (!customer.getPassword().equals(encryptedPassword)) {
            logger.error("Bad attempt to login: {}", email);
            throw new UnauthorizedException("Wrong email or password!");
        }

        logger.info("Customer {} login successfully", email);
        return customer;
    }

    @Override
    public Customer register(RegistrationDTO registrationDTO) {
        final var email = registrationDTO.email();
        final var password = registrationDTO.password();

        ValidationUtils.checkThat(Objects.nonNull(email) && Objects.nonNull(password)
            , () -> new BadRequestException("Email or Password is null."));

        final var savedCustomer = customerRepository.save(
            customer()
                .name(registrationDTO.name())
                .surname(registrationDTO.surname())
                .email(registrationDTO.email())
                .password(PasswordEncryptor.encryptPassword(registrationDTO.password()))
                .build()
        );

        logger.info("Customer {} was successfully saved", savedCustomer.getId());
        return savedCustomer;
    }
}
