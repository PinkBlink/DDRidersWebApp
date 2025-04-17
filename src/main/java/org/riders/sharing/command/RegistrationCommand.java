package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.RegistrationDTO;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

public class RegistrationCommand extends Command {
    private static final Logger logger = LogManager.getLogger(RegistrationCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var customerService = new CustomerServiceImpl(new CustomerRepositoryImpl(ConnectionPool.INSTANCE));
            final var requestBody = ServletUtils.getRequestBody(request);
            final var registrationDTO = new ObjectMapper().readValue(requestBody, RegistrationDTO.class);
            final var newCustomer = Customer.Builder.customer()
                .name(registrationDTO.getName())
                .surname(registrationDTO.getSurname())
                .email(registrationDTO.getEmail())
                .password(registrationDTO.getPassword())
                .build();

            customerService.register(newCustomer);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IOException e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (DuplicateEntryException e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);

        } catch (InvalidCredentialsException e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
