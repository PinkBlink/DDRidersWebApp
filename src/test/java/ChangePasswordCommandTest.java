import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.ChangePasswordCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangePasswordCommandTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand(customerService);

    @Test
    public void changePasswordRespondsWith202() throws IOException {
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);
        final var oldPassword = "password";
        final var newPassword = "new_password";
        final var oldPasswordHash = PasswordEncryptor.encryptPassword(oldPassword);
        final var customer = aCustomer().password(oldPasswordHash).build();
        customerRepository.save(customer);
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
        final var expectedRespStatus = HttpServletResponse.SC_ACCEPTED;
        final var expectedNewPasswordHash = PasswordEncryptor.encryptPassword(newPassword);


        when(request.getReader()).thenReturn(requestReader);
        changePasswordCommand.execute(request, response);

        verify(response).setStatus(expectedRespStatus);
        final var updatedCustomer = customerService.getById(customer.getId());
        Assertions.assertEquals(expectedNewPasswordHash, updatedCustomer.getPassword());
    }
}
