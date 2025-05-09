package org.riders.sharing.command;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.authentication.AuthTokenGenerator;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.dto.UpdateTokensDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ModelMapper;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class UpdateTokensCommand extends Command {
    private final Logger logger = LogManager.getLogger(UpdateTokensCommand.class);
    private final CustomerService customerService;
    private final AuthTokenDecoder tokenDecoder;
    private final AuthTokenGenerator tokenGenerator;

    public UpdateTokensCommand(CustomerService customerService, AuthTokenDecoder tokenDecoder, AuthTokenGenerator tokenGenerator) {
        this.customerService = customerService;
        this.tokenDecoder = tokenDecoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = getRequestBody(request);
        final var updateTokensDto = ModelMapper.parse(requestBody, UpdateTokensDto.class);

        final var decodedRefresh = tokenDecoder.decode(updateTokensDto.refreshToken());

        final var customerId = tokenDecoder.getIdFromToken(decodedRefresh);
        final var customerFromDb = customerService.getById(customerId);

        final var newAccessToken = tokenGenerator.generateNewAccessToken(customerFromDb);
        final var newRefreshToken = tokenGenerator.generateNewRefreshToken(customerFromDb);

        final var tokenDto = new TokenDto(newAccessToken, newRefreshToken);
        final var tokensJson = ModelMapper.toJsonString(tokenDto);

        response.setStatus(SC_OK);
        response.setContentType(JSON_CONTENT_TYPE);

        writeResponse(response, tokensJson);
    }
}
