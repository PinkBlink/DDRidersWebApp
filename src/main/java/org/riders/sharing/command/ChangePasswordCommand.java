package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ChangePasswordDto;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

public class ChangePasswordCommand extends Command {
    private final Logger logger = LogManager.getLogger(ChangePasswordCommand.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());
    private final CustomerService customerService;

    public ChangePasswordCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var changePassDto = objectMapper.readValue(requestBody, ChangePasswordDto.class);

            final var updatedCustomer = customerService.changePassword(changePassDto);
            final var customerDtoToResponse = CustomerDto.fromCustomer(updatedCustomer);
            final var jsonCustomerDto = objectMapper.writeValueAsString(customerDtoToResponse);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType(JSON_CONTENT_TYPE);

            try (final var writer = response.getWriter()) {
                writer.write(jsonCustomerDto);
            }
        } catch (NoElementException e) {
            logger.error("Failed to find customer: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (UnauthorizedException e) {
            logger.error("Failed attempt to change password due to unauthorized access: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            logger.error("Failed attempt to change password due to bad request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed attempt to change password with message: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
