package org.riders.sharing.service;

import org.riders.sharing.dto.ChangePasswordDto;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.dto.RegistrationDto;
import org.riders.sharing.model.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer login(LoginDto loginDto);

    Customer register(RegistrationDto registrationDto);

    Customer changePassword(ChangePasswordDto changePasswordDto);

    Customer getById(UUID customerId);
}
