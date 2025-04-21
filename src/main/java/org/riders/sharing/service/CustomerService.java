package org.riders.sharing.service;

import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.dto.RegistrationDto;
import org.riders.sharing.model.Customer;

public interface CustomerService {
    Customer login(LoginDto loginDto);

    Customer register(RegistrationDto registrationDto);
}
