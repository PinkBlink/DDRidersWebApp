import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.RegistrationCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationCommandTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final RegistrationCommand registrationCommand = new RegistrationCommand(customerService);

    @Test
    public void registerRespondsWith201() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var newCustomer = aCustomer().build();

        final var jsonAsReader = new StringReader("""
            {
            "name" : "%s",
            "surname" : "%s",
            "email" : "%s",
            "password" : "%s"
            }
            """
            .formatted(
                newCustomer.getName(),
                newCustomer.getSurname(),
                newCustomer.getEmail(),
                newCustomer.getPassword())
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponse = HttpServletResponse.SC_CREATED;

        when(request.getReader()).thenReturn(requestReader);

        //when
        registrationCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerRespondsWith409IfAlreadyExist() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var newCustomer = aCustomer().build();

        final var jsonAsReader = new StringReader("""
            {
            "name" : "%s",
            "surname" : "%s",
            "email" : "%s",
            "password" : "%s"
            }
            """
            .formatted(
                newCustomer.getName(),
                savedCustomer.getSurname(),
                savedCustomer.getEmail(),
                savedCustomer.getPassword())
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponse = HttpServletResponse.SC_CONFLICT;

        when(request.getReader()).thenReturn(requestReader);

        //when
        registrationCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerRespondsWith400IfEmailOrPassIsNull() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var newCustomer = aCustomer().build();

        final var stringReader = new StringReader("""
            {
            "name" : "%s",
            "surname" : "%s",
            "password" : "%s"
            }
            """
            .formatted(
                newCustomer.getName(),
                savedCustomer.getSurname(),
                savedCustomer.getPassword()
            )
        );
        final var requestReader = new BufferedReader(stringReader);

        final var expectedResponse = HttpServletResponse.SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);

        //when
        registrationCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);
    }

    @Test
    public void registerRespondsWith500IfException() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var expectedStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(new NullPointerException("General Exception check"));

        //when
        registrationCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedStatus);
    }
}
