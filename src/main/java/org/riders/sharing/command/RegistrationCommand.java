package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.RegistrationDTO;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

public class RegistrationCommand extends Command {
    private static final Logger logger = LogManager.getLogger(RegistrationCommand.class);

    private final CustomerService customerService;

    public RegistrationCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var registrationDTO = new ObjectMapper().readValue(requestBody, RegistrationDTO.class);

            customerService.register(registrationDTO);

            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (DuplicateEntryException e) {
            logger.error("Registration failed due to duplicate entry: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (BadRequestException e) {
            logger.error("Registration failed due to bad request: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Registration failed with message: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
