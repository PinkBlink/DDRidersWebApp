import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.ChangePasswordCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangePasswordCommandTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand(customerService);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void changePasswordRespondsWith201AndCustomer() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var oldPassword = "password";
        final var newPassword = "new_password";
        final var oldPasswordHash = PasswordEncryptor.encryptPassword(oldPassword);
        final var customer = customerRepository.save(
            aCustomer()
                .password(oldPasswordHash)
                .build()
        );

        final var jsonAsReader = new StringReader("""
            {
            "customerId" : "%s",
            "oldPassword" : "%s",
            "newPassword" : "%s"
            }"""
            .formatted(
                customer.getId(),
                oldPassword,
                newPassword
            )
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedRespStatus = SC_CREATED;
        final var expectedNewPasswordHash = PasswordEncryptor.encryptPassword(newPassword);

        //when
        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);

        final var updatedCustomer = customerService.getById(customer.getId());
        final var customerDtoFromResponse = objectMapper.readValue(stringWriter.toString(), CustomerDto.class);

        assertEquals(expectedNewPasswordHash, updatedCustomer.getPassword());
        assertEquals(updatedCustomer.getId(), customerDtoFromResponse.id());
        assertEquals(updatedCustomer.getName(), customerDtoFromResponse.name());
        assertEquals(updatedCustomer.getSurname(), customerDtoFromResponse.surname());
        assertEquals(updatedCustomer.getEmail(), customerDtoFromResponse.email());
    }

    @Test
    public void changePasswordRespondsWith404() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var nonExistentCustomerId = UUID.randomUUID();
        final var jsonAsReader = new StringReader("""
            {
            "customerId" : "%s",
            "oldPassword" : "old",
            "newPassword" : "new"
            }"""
            .formatted(nonExistentCustomerId)
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedRespStatus = SC_NOT_FOUND;

        //when
        when(request.getReader()).thenReturn(requestReader);
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);
    }

    @Test
    public void changePasswordRespondsWith401() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var customer = customerRepository.save(aCustomer().build());
        final var incorrectPass = "clean_pussy_walkers";

        final var jsonAsReader = new StringReader("""
            {
            "customerId" : "%s",
            "oldPassword" : "%s",
            "newPassword" : "new"
            }"""
            .formatted(customer.getId(), incorrectPass)
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedRespStatus = SC_UNAUTHORIZED;

        //when
        when(request.getReader()).thenReturn(requestReader);
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);
    }

    @Test
    public void changePasswordRespondsWith400() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var customer = customerRepository.save(aCustomer().build());

        final var jsonAsReader = new StringReader("""
            {
            "customerId" : "%s",
            "newPassword" : "new"
            }"""
            .formatted(customer.getId())
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedRespStatus = SC_BAD_REQUEST;

        //when
        when(request.getReader()).thenReturn(requestReader);
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);
    }

    @Test
    public void changePasswordRespondsWith500() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var expectedRespStatus = SC_INTERNAL_SERVER_ERROR;

        //when
        when(request.getReader()).thenThrow(RuntimeException.class);
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);
    }
}
