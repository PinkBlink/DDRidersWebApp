import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerServiceTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);

    @Test
    public void loginThrowsUnauthorizedIfWrongPass() {
        final var customer = customerRepository.save(aCustomer().build());

        final var loginDto = new LoginDto(customer.getEmail(), "wrong_pass");

        assertThrows(UnauthorizedException.class, () -> customerService.login(loginDto));
    }

    @Test
    public void loginThrowsUnauthorizedIfWrongEmail() {
        final var customer = customerRepository.save(aCustomer().build());

        final var loginDto = new LoginDto("wrong@email", customer.getPassword());

        assertThrows(UnauthorizedException.class,
            () -> customerService.login(loginDto));
    }

    @Test
    public void loginReturnsCustomer() {
        final var customer = customerRepository.save(aCustomer().build());
        final var loginDto = new LoginDto(customer.getEmail(), customer.getPassword());

        final var loggedCustomer = customerService.login(loginDto);

        assertEquals(customer, loggedCustomer);
    }

}
