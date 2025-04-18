import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.LoginCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.*;

public class LoginCommandTest extends BaseTest implements CustomerTestData {
    private final LoginCommand loginCommand = new LoginCommand(
        new CustomerServiceImpl(new CustomerRepositoryImpl(ConnectionPool.INSTANCE))
    );

    @Test
    public void loginRespondsWith200() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var savedCustomer = new CustomerRepositoryImpl(ConnectionPool.INSTANCE).save(aCustomer().build());
        final var jsonAsReader = new StringReader("""
            {
               "email" : "%s",
               "password" : "%s"
            }
            """.formatted(savedCustomer.getEmail(), savedCustomer.getPassword()));
        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponse = HttpServletResponse.SC_OK;

        when(request.getReader()).thenReturn(requestReader);
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith401Unauthorized() throws IOException {
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
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith400IfEmptyRequest() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var emptyJson = new StringReader("""
            """);
        final var requestReader = new BufferedReader(emptyJson);
        final var expectedResponse = HttpServletResponse.SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginRespondsWith500IfException() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var expectedResponse = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(new IOException("General Exception check"));
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }
}
