import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.LoginDto;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerServiceTest extends BaseTest implements CustomerTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);

    @Test
    public void loginThrowsUnauthorizedIfWrongPass() {
        //given
        final var customer = customerRepository.save(aCustomer().build());

        final var loginDto = new LoginDto(customer.getEmail(), "wrong_pass");

        //then
        assertThrows(UnauthorizedException.class, () -> customerService.login(loginDto));
    }

    @Test
    public void loginThrowsUnauthorizedIfWrongEmail() {
        //given
        final var customer = customerRepository.save(aCustomer().build());

        final var loginDto = new LoginDto("wrong@email", customer.getPassword());

        //then
        assertThrows(UnauthorizedException.class,
            () -> customerService.login(loginDto));
    }

    @Test
    public void loginReturnsCustomer() {
        //given
        final var password = "password";
        final var hashedPassword = PasswordEncryptor.encryptPassword(password);
        final var customer = customerRepository.save(aCustomer().password(hashedPassword).build());

        final var loginDto = new LoginDto(customer.getEmail(), password);

        //when
        final var loggedCustomer = customerService.login(loginDto);

        //then
        assertEquals(customer, loggedCustomer);
    }

}
