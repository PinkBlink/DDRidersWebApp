package org.riders.sharing.service;

import org.riders.sharing.dto.LoginDTO;
import org.riders.sharing.dto.RegistrationDTO;
import org.riders.sharing.model.Customer;

public interface CustomerService {
    Customer login(LoginDTO loginDTO);

    Customer register(RegistrationDTO registrationDTO);
}
