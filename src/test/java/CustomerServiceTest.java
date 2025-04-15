import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.InvalidCredentialsException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerServiceTest extends BaseTest implements CustomerTestData {
    private final CustomerService customerService = new CustomerServiceImpl(
        new CustomerRepositoryImpl(ConnectionPool.INSTANCE));

    @Test
    public void registerCustomerToDb() {
        final var customer = aCustomer().build();

        final var registeredCustomer = customerService.register(customer);

        assertEquals(customer, registeredCustomer);
    }

    @Test
    public void getByIdReturnsCustomer() {
        final var customer = customerService.register(aCustomer().build());

        final var customerFromDb = customerService.getById(customer.getId());

        assertEquals(customer, customerFromDb);
    }

    @Test
    public void getByIdThrowsNoElementIfWrongId(){
        final var randomId = UUID.randomUUID();

        assertThrows(NoElementException.class,
            () ->  customerService.getById(randomId));
    }

    @Test
    public void logInThrowsInvalidCredentialsIfWrongPass() {
        final var customer = customerService.register(aCustomer().build());

        final var wrongPass = "000";

        assertThrows(InvalidCredentialsException.class, () -> customerService.login(customer.getEmail(), wrongPass));
    }

    @Test
    public void loginThrowsInvalidCredentialsIfWrongEmail() {
        final var customer = customerService.register(aCustomer().build());

        final var wrongEmail = customer.getEmail().toUpperCase();

        assertThrows(InvalidCredentialsException.class,
            () -> customerService.login(wrongEmail, customer.getPassword()));
    }

    @Test
    public void loginReturnsCustomer() {
        final var customer = customerService.register(aCustomer().build());

        final var loggedCustomer = customerService.login(customer.getEmail(), customer.getPassword());

        assertEquals(customer, loggedCustomer);
    }

    @Test
    public void changePasswordUpdatesCustomer() {
        final var customer = customerService.register(aCustomer().build());
        final var newPass = "Dirty_Dick_Riders_Forever";

        customerService.changePassword(customer.getId(), customer.getPassword(), newPass);
        final var updatedCustomer = customerService.getById(customer.getId());

        assertEquals(newPass, updatedCustomer.getPassword());
    }

    @Test
    public void changePasswordThrowsIfWrongPass() {
        final var customer = customerService.register(aCustomer().build());

        final var wrongOldPass = "Clean_Pussy_Runners_Forever";

        assertThrows(InvalidCredentialsException.class,
            () -> customerService.changePassword(customer.getId(), wrongOldPass, "123"));
    }
}
