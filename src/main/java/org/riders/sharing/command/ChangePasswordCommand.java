package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ChangePasswordDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

public class ChangePasswordCommand extends Command {
    private static final Logger logger = LogManager.getLogger(ChangePasswordCommand.class);
    private final CustomerService customerService;

    public ChangePasswordCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var objectMapper = new ObjectMapper();
            final var requestBody = ServletUtils.getRequestBody(request);
            final var changePassDto = objectMapper.readValue(requestBody, ChangePasswordDto.class);
            customerService.changePassword(changePassDto);

            response.setStatus(HttpServletResponse.SC_ACCEPTED);
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
