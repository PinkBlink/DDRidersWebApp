package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.dto.ModelMapper;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ApplicationConfig;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.authentication.AuthTokenGenerator;

public class LoginCommand extends Command {
    private final Logger logger = LogManager.getLogger(LoginCommand.class);
    private final ModelMapper modelMapper = ModelMapper.INSTANCE;
    private final CustomerService customerService;

    public LoginCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var loginDto = modelMapper.getAsObject(requestBody, LoginDto.class);
            final var customer = customerService.login(loginDto);

            final var appConfig = ApplicationConfig.getInstance();

            final var tokenGenerator = new AuthTokenGenerator(
                appConfig.getAccessTokenTtl(),
                appConfig.getRefreshTokenTtl(),
                appConfig.getAlgorithm()
            );

            final var accessToken = tokenGenerator.generateNewAccessToken(customer);
            final var refreshToken = tokenGenerator.generateNewRefreshToken(customer);
            final var tokensDto = new TokenDto(accessToken, refreshToken);
            final var json = modelMapper.getAsJson(tokensDto);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(JSON_CONTENT_TYPE);

            try (final var writer = response.getWriter()) {
                writer.write(json);
            }
        } catch (UnauthorizedException e) {
            logger.error("Login failed due to unauthorized access: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            logger.error("Login failed due to bad request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Login failed with message: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
