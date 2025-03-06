package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.RegistrationDTO;
import org.riders.sharing.exception.UserExistsException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

public class RegistrationCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());

        try {
            String body = ServletUtils.getRequestBody(request);

            RegistrationDTO registrationDTO = objectMapper.readValue(body, RegistrationDTO.class);

            Customer newCustomer = Customer.Builder.getNewBuilderWithId()
                    .setName(registrationDTO.getName())
                    .setSurname(registrationDTO.getSurname())
                    .setEmail(registrationDTO.getEmail())
                    .setPassword(PasswordEncryptor.hashPassword(registrationDTO.getPassword()))
                    .build();

            customerService.register(newCustomer);
            response.setStatus(HttpServletResponse.SC_CREATED);

        } catch (UserExistsException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_CONFLICT, e);
        }
    }
}

