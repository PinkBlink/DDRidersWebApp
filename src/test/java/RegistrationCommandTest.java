import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.RegistrationCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationCommandTest implements CustomerTestData {
    private final RegistrationCommand registrationCommand = new RegistrationCommand();

    @Test
    public void registerSetsStatus201() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var newCustomer = aCustomer().build();
        final var expectedResponse = HttpServletResponse.SC_CREATED;
        final var requestReader = new BufferedReader(
            new StringReader("""
                {
                "name" : "%s",
                "surname" : "%s",
                "email" : "%s",
                "password" : "%s"
                }
                """
                .formatted(newCustomer.getName(),
                    newCustomer.getSurname(),
                    newCustomer.getEmail(),
                    newCustomer.getPassword())));

        when(request.getReader()).thenReturn(requestReader);
        registrationCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerSetsStatus409IfAlreadyExist() throws IOException {
        final var savedCustomer = new CustomerRepositoryImpl(ConnectionPool.INSTANCE).save(aCustomer().build());
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var newCustomer = aCustomer().build();
        final var expectedResponse = HttpServletResponse.SC_CONFLICT;
        final var requestReader = new BufferedReader(
            new StringReader("""
                {
                "name" : "%s",
                "surname" : "%s",
                "email" : "%s",
                "password" : "%s"
                }
                """
                .formatted(newCustomer.getName(),
                    savedCustomer.getSurname(),
                    savedCustomer.getEmail(),
                    savedCustomer.getPassword())));

        when(request.getReader()).thenReturn(requestReader);
        registrationCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerSetsStatus400IfEmailOrPassIsNull() throws IOException {
        final var savedCustomer = new CustomerRepositoryImpl(ConnectionPool.INSTANCE).save(aCustomer().build());
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var newCustomer = aCustomer().build();
        final var expectedResponse = HttpServletResponse.SC_BAD_REQUEST;
        final var requestReader = new BufferedReader(
            new StringReader("""
                {
                "name" : "%s",
                "surname" : "%s",
                "password" : "%s"
                }
                """
                .formatted(newCustomer.getName(),
                    savedCustomer.getSurname(),
                    savedCustomer.getPassword())));

        when(request.getReader()).thenReturn(requestReader);
        registrationCommand.execute(request, response);

        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerSetsStatus500OnIOException() throws IOException {
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var expectedStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(new IOException("IOException check"));
        registrationCommand.execute(request, response);

        verify(response).setStatus(expectedStatus);
    }
}
