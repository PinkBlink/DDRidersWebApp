import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.LoginCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.dto.TokenDto;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.config.ApplicationConfig;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.authentication.AuthTokenDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginCommandTest extends BaseTest implements CustomerTestData {
    private final ApplicationConfig appConfig = ApplicationConfig.getInstance();
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final LoginCommand loginCommand = new LoginCommand(new CustomerServiceImpl(customerRepository));
    private final AuthTokenDecoder authTokenDecoder = new AuthTokenDecoder(appConfig.getAlgorithm());
    private final String password = "password";
    private final String hashedPassword = PasswordEncryptor.encryptPassword(password);

    @Test
    public void loginRespondsWith200() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().password(hashedPassword).build());
        final var jsonAsReader = new StringReader("""
            {
               "email" : "%s",
               "password" : "%s"
            }
            """.formatted(savedCustomer.getEmail(), password));

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponse = HttpServletResponse.SC_OK;

        when(request.getReader()).thenReturn(requestReader);

        //when
        loginCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith401Unauthorized() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var jsonAsReader = new StringReader("""
            {
               "email" : "wrong",
               "password" : "wrong"
            }
            """);

        final var requestReader = new BufferedReader(jsonAsReader);

        final var expectedResponse = HttpServletResponse.SC_UNAUTHORIZED;

        when(request.getReader()).thenReturn(requestReader);

        //when
        loginCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith400IfEmptyRequest() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var emptyJson = new StringReader("");
        final var requestReader = new BufferedReader(emptyJson);

        final var expectedResponse = HttpServletResponse.SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);

        //when
        loginCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith500IfException() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var expectedResponse = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(new IOException("IOException check"));

        //when
        loginCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWithTokens() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var customer = aCustomer().password(hashedPassword).build();
        final var savedCustomer = customerRepository.save(customer);

        final var jsonAsReader = new StringReader("""
            {
               "email" : "%s",
               "password" : "%s"
            }
            """.formatted(savedCustomer.getEmail(), password));

        final var requestReader = new BufferedReader(jsonAsReader);
        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        loginCommand.execute(request, response);

        //then
        final var tokens = ModelMapper.parse(stringWriter.toString(), TokenDto.class);
        final var decodedAccessToken = authTokenDecoder.decode(tokens.accessToken());
        final var idFromAccessToken = UUID.fromString(decodedAccessToken.getSubject());
        final var decodedRefreshToken = authTokenDecoder.decode(tokens.refreshToken());
        final var idFromRefreshToken = UUID.fromString(decodedRefreshToken.getSubject());

        assertEquals(savedCustomer.getId(), idFromRefreshToken);
        assertEquals(savedCustomer.getId(), idFromAccessToken);
    }
}
