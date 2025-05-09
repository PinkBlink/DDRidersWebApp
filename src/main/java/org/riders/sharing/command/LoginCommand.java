package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.config.ApplicationConfig;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.authentication.AuthTokenGenerator;

import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class LoginCommand extends Command {
    private final CustomerService customerService;

    public LoginCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = ServletUtils.getRequestBody(request);
        final var loginDto = ModelMapper.parse(requestBody, LoginDto.class);
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
        final var jsonResponse = ModelMapper.toJsonString(tokensDto);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(JSON_CONTENT_TYPE);

        writeResponse(response, jsonResponse);
    }
}