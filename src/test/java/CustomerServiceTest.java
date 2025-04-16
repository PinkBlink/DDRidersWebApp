import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerServiceTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);

    @Test
    public void logInThrowsInvalidCredentialsIfWrongPass() {
        final var customer = customerRepository.save(aCustomer().build());

        final var wrongPass = "000";

        assertThrows(InvalidCredentialsException.class, () -> customerService.login(customer.getEmail(), wrongPass));
    }

    @Test
    public void loginThrowsInvalidCredentialsIfWrongEmail() {
        final var customer = customerRepository.save(aCustomer().build());

        final var wrongEmail = customer.getEmail().toUpperCase();

        assertThrows(InvalidCredentialsException.class,
            () -> customerService.login(wrongEmail, customer.getPassword()));
    }

    @Test
    public void loginReturnsCustomer() {
        final var customer = customerRepository.save(aCustomer().build());

        final var loggedCustomer = customerService.login(customer.getEmail(), customer.getPassword());

        assertEquals(customer, loggedCustomer);
    }

}
