package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

public class LoginCommand extends Command {
    private final static Logger logger = LogManager.getLogger(LoginCommand.class);

    private final CustomerService customerService;

    public LoginCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var loginDto = new ObjectMapper().readValue(requestBody, LoginDto.class);

            customerService.login(loginDto);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (UnauthorizedException e) {
            logger.error("Login failed due to unauthorized access: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            logger.error("Login failed due to bad request: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Login failed with message: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
