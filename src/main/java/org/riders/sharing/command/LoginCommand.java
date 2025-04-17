package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.LoginDTO;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.exception.InvalidRequestException;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

public class LoginCommand extends Command {
    private final static Logger logger = LogManager.getLogger(LoginCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var customerService = new CustomerServiceImpl(
                new CustomerRepositoryImpl(ConnectionPool.INSTANCE)
            );
            final var requestBody = ServletUtils.getRequestBody(request);
            final var loginDto = new ObjectMapper().readValue(requestBody, LoginDTO.class);

            customerService.login(loginDto.getEmail(), loginDto.getPassword());

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | InvalidRequestException | InvalidCredentialsException e) {
            logger.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
