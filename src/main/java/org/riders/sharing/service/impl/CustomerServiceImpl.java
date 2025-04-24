package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.dto.RegistrationDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ValidationUtils;

import java.util.Objects;

import static org.riders.sharing.model.Customer.Builder.customer;
import static org.riders.sharing.utils.PasswordEncryptor.encryptPassword;

public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer login(LoginDto loginDto) {
        final var email = loginDto.email();
        final var password = loginDto.password();

        ValidationUtils.checkThat(Objects.nonNull(email) && Objects.nonNull(password)
            , () -> new BadRequestException("Email or Password is null."));

        final var customer = customerRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Bad attempt to login: {}", email);
            return new UnauthorizedException("Wrong email or password!");
        });

        final var hashedPassword = encryptPassword(password);

        if (!customer.getPassword().equals(hashedPassword)) {
            logger.error("Bad attempt to login: {}", email);
            throw new UnauthorizedException("Wrong email or password!");
        }

        logger.info("Customer {} login successfully", email);
        return customer;
    }

    @Override
    public Customer register(RegistrationDto registrationDto) {
        final var email = registrationDto.email();
        final var password = registrationDto.password();

        ValidationUtils.checkThat(Objects.nonNull(email) && Objects.nonNull(password)
            , () -> new BadRequestException("Email or Password is null."));

        final var hashedPassword = encryptPassword(password);
        final var savedCustomer = customerRepository.save(
            customer()
                .name(registrationDto.name())
                .surname(registrationDto.surname())
                .email(registrationDto.email())
                .password(hashedPassword)
                .build()
        );

        logger.info("Customer {} was successfully saved", savedCustomer.getId());
        return savedCustomer;
    }
}
