package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.authentication.AuthTokenGenerator;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.dto.UpdateTokensDto;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ModelMapper;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class UpdateTokensCommand extends Command {
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
