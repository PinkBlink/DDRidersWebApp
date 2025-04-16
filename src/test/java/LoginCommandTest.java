import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.LoginCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class LoginCommandTest extends BaseTest implements CustomerTestData {
    private final LoginCommand loginCommand = new LoginCommand();

    @Test
    public void loginSetsResponseStatus200() throws IOException {
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var expectedResponse = HttpServletResponse.SC_OK;
        final var customerRepo = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
        final var customer = customerRepo.save(aCustomer().build());

        Mockito.when(request.getReader()).thenReturn(
            new BufferedReader(
                new StringReader("""
                    {
                       "email" : "%s",
                       "password" : "%s"
                    }
                    """.formatted(customer.getEmail(), customer.getPassword()))));
        loginCommand.execute(request, response);

        Mockito.verify(response).setStatus(expectedResponse);
    }

    @Test
    public void loginSetsStatus401IfInvalidCredo() throws IOException {
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var expectedResponse = HttpServletResponse.SC_UNAUTHORIZED;

        Mockito.when(request.getReader()).thenReturn(
            new BufferedReader(
                new StringReader("""
                    {
                       "email" : "wrong",
                       "password" : "wrong"
                    }
                    """)));
        loginCommand.execute(request, response);

        Mockito.verify(response).setStatus(expectedResponse);
    }
}
