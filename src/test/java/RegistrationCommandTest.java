import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.command.RegistrationCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.ModelMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedResponse = SC_CREATED;

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        registrationCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponse);

        final var customerDto = ModelMapper.parse(stringWriter.toString(), CustomerDto.class);
        final var customerDataBase = customerService.getById(customerDto.id());

        assertEquals(newCustomer.getName(), customerDataBase.getName());
        assertEquals(newCustomer.getEmail(), customerDataBase.getEmail());
        assertEquals(newCustomer.getSurname(), customerDataBase.getSurname());
    }

    @Test
    public void registerThrowsDuplicate() throws IOException {
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

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            DuplicateEntryException.class,
            () -> registrationCommand.execute(request, response)
        );
    }

    @Test
    public void registerThrowsBadRequest() throws IOException {
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

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            BadRequestException.class,
            () -> registrationCommand.execute(request, response)
        );
    }
}
