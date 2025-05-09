package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ChangePasswordDto;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.dto.RegistrationDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ErrorMessages;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.utils.ValidationUtils;

import java.util.Objects;
import java.util.UUID;

import static org.riders.sharing.model.Customer.Builder.customer;
import static org.riders.sharing.utils.ErrorMessages.CUSTOMER_NOT_FOUND;
import static org.riders.sharing.utils.ErrorMessages.NULL_EMAIL_OR_PASSWORD;
import static org.riders.sharing.utils.ErrorMessages.NULL_ID_OR_PASSWORD;
import static org.riders.sharing.utils.ErrorMessages.WRONG_EMAIL_PASSWORD;
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

        ValidationUtils.checkThat(
            Objects.nonNull(email) && Objects.nonNull(password),
            () -> new BadRequestException(NULL_EMAIL_OR_PASSWORD)
        );

        final var customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.error("Bad attempt to login: {}", email);
                return new UnauthorizedException(CUSTOMER_NOT_FOUND.formatted(email));
            });

        final var hashedPassword = encryptPassword(password);

        if (!customer.getPassword().equals(hashedPassword)) {
            logger.error("Bad attempt to login: {}", email);
            throw new UnauthorizedException(WRONG_EMAIL_PASSWORD);
        }

        logger.info("Customer {} login successfully", email);
        return customer;
    }

    @Override
    public Customer register(RegistrationDto registrationDto) {
        final var email = registrationDto.email();
        final var password = registrationDto.password();

        ValidationUtils.checkThat(
            Objects.nonNull(email) && Objects.nonNull(password),
            () -> new BadRequestException(NULL_EMAIL_OR_PASSWORD)
        );

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

    @Override
    public Customer changePassword(ChangePasswordDto changePasswordDto) {
        ValidationUtils.checkThat(
            Objects.nonNull(changePasswordDto.customerId())
                && Objects.nonNull(changePasswordDto.newPassword())
                && Objects.nonNull(changePasswordDto.oldPassword()),
            () -> new BadRequestException(NULL_ID_OR_PASSWORD)
        );

        final var customerFromDb = getById(UUID.fromString(changePasswordDto.customerId()));
        final var oldPassHash = PasswordEncryptor.encryptPassword(changePasswordDto.oldPassword());
        final var newPassHash = PasswordEncryptor.encryptPassword(changePasswordDto.newPassword());

        if (oldPassHash.equals(customerFromDb.getPassword())) {
            final var updatedCustomer = customerRepository.update(
                customerFromDb.toBuilder()
                    .password(newPassHash)
                    .build()
            );

            logger.info("Successfully changed password for customer with id {}", customerFromDb.getId());
            return updatedCustomer;
        }

        logger.error("Old password does not match: {}", changePasswordDto.customerId());
        throw new UnauthorizedException(WRONG_EMAIL_PASSWORD);
    }

    @Override
    public Customer getById(UUID customerId) {
        final var maybeCustomer = customerRepository.findById(customerId);
        return maybeCustomer.orElseThrow(
            () -> {
                logger.error("Couldn't find customer with id: {}", customerId);
                return new NotFoundException(CUSTOMER_NOT_FOUND.formatted(customerId));
            }
        );
    }
}
