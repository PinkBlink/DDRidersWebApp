import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.authentication.AuthTokenGenerator;
import org.riders.sharing.command.UpdateTokensCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.dto.UpdateTokensDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.config.ApplicationConfig;
import org.riders.sharing.utils.ModelMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateTokensCommandTest extends BaseTest implements CustomerTestData {
    private final ApplicationConfig config = ApplicationConfig.getInstance();
    private final AuthTokenDecoder authTokenDecoder = new AuthTokenDecoder(config.getAlgorithm());
    private final AuthTokenGenerator authTokenGenerator = new AuthTokenGenerator(
        config.getAccessTokenTtl(),
        config.getRefreshTokenTtl(),
        config.getAlgorithm()
    );

    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final org.riders.sharing.service.CustomerService customerService = new CustomerServiceImpl(customerRepository);

    private final UpdateTokensCommand updateTokensCommand = new UpdateTokensCommand(customerService,
        authTokenDecoder,
        authTokenGenerator);

    @Test
    public void upgradeTokensRespondsWith200AndTokens() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());

        final var refreshToken = authTokenGenerator.generateNewRefreshToken(savedCustomer);
        final var updateTokensDto = new UpdateTokensDto(refreshToken);
        final var updateJson = ModelMapper.toJsonString(updateTokensDto);

        final var stringReader = new StringReader(updateJson);
        final var requestReader = new BufferedReader(stringReader);

        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedResponseStatus = SC_OK;

        //when
        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        updateTokensCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);

        final var tokensFromResponse = ModelMapper.parse(stringWriter.toString(), TokenDto.class);

        Assertions.assertDoesNotThrow(() -> authTokenDecoder.decode(tokensFromResponse.accessToken()));
        Assertions.assertDoesNotThrow(() -> authTokenDecoder.decode(tokensFromResponse.refreshToken()));
    }

    @Test
    public void upgradeTokensThrowsBadRequest() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var stringReader = new StringReader("{}");
        final var requestReader = new BufferedReader(stringReader);

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            BadRequestException.class,
            () -> updateTokensCommand.execute(request, response)
        );
    }

    @Test
    public void upgradeTokensThrowsTokenExpired() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var customer = aCustomer().build();
        final var expiredToken = JWT.create()
            .withSubject(customer.getId().toString())
            .withExpiresAt(Instant.now())
            .sign(config.getAlgorithm());

        final var updateTokensDto = new UpdateTokensDto(expiredToken);
        final var updateJson = ModelMapper.toJsonString(updateTokensDto);

        final var stringReader = new StringReader(updateJson);
        final var requestReader = new BufferedReader(stringReader);

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            TokenExpiredException.class,
            () -> updateTokensCommand.execute(request, response)
        );
    }

    @Test
    public void upgradeTokensThrowsNotFound() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var unsavedCustomer = aCustomer().build();
        final var refreshToken = authTokenGenerator.generateNewRefreshToken(unsavedCustomer);

        final var updateTokensDto = new UpdateTokensDto(refreshToken);
        final var updateJson = ModelMapper.toJsonString(updateTokensDto);

        final var stringReader = new StringReader(updateJson);
        final var requestReader = new BufferedReader(stringReader);

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            NotFoundException.class,
            () -> updateTokensCommand.execute(request, response)
        );
    }
}
