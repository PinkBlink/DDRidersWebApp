import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.ChangePasswordCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.utils.ModelMapper;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangePasswordCommandTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand(customerService);

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

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        changePasswordCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedRespStatus);

        final var updatedCustomer = customerService.getById(customer.getId());
        final var customerDtoFromResponse = ModelMapper.parse(stringWriter.toString(), CustomerDto.class);

        assertEquals(expectedNewPasswordHash, updatedCustomer.getPassword());
        assertEquals(updatedCustomer.getId(), customerDtoFromResponse.id());
        assertEquals(updatedCustomer.getName(), customerDtoFromResponse.name());
        assertEquals(updatedCustomer.getSurname(), customerDtoFromResponse.surname());
        assertEquals(updatedCustomer.getEmail(), customerDtoFromResponse.email());
    }

    @Test
    public void changePasswordThrowsNotFound() throws IOException {
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

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        assertThrows(
            NotFoundException.class,
            () -> changePasswordCommand.execute(request, response)
        );
    }

    @Test
    public void changePasswordThrowsUnauthorized() throws IOException {
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

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        assertThrows(
            UnauthorizedException.class,
            () -> changePasswordCommand.execute(request, response)
        );
    }

    @Test
    public void changePasswordThrowsBadRequest() throws IOException {
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

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        assertThrows(
            BadRequestException.class,
            () -> changePasswordCommand.execute(request, response)
        );
    }
}
