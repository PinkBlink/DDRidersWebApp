import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.LoginCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.*;

public class LoginCommandTest extends BaseTest implements CustomerTestData {
    private final LoginCommand loginCommand = new LoginCommand();

    @Test
    public void loginSetsResponseStatus200() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var savedCustomer = new CustomerRepositoryImpl(ConnectionPool.INSTANCE).save(aCustomer().build());
        final var expectedResponse = HttpServletResponse.SC_OK;
        final var requestReader = new BufferedReader(
            new StringReader("""
                {
                   "email" : "%s",
                   "password" : "%s"
                }
                """.formatted(savedCustomer.getEmail(), savedCustomer.getPassword())));

        when(request.getReader()).thenReturn(requestReader);
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginSetsStatus401IfInvalidCredo() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var expectedResponse = HttpServletResponse.SC_UNAUTHORIZED;
        final var requestReader = new BufferedReader(
            new StringReader("""
                {
                   "email" : "wrong",
                   "password" : "wrong"
                }
                """));

        when(request.getReader()).thenReturn(requestReader);
        loginCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }
}
