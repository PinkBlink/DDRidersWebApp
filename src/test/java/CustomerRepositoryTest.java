import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerRepositoryTest extends BaseTest {
    private static final Customer CUSTOMER = CustomerTestData.aCustomer().build();

    private  final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);

    @Test
    public void saveSetCreateAndUpdateTime() {
        final var savedCustomer = customerRepository.save(CUSTOMER);

        assertTrue(savedCustomer.getCreateTime().equals(savedCustomer.getUpdateTime())
            && CUSTOMER.equals(savedCustomer));
    }

    @Test
    public void saveThrowsDuplicateExceptionIfEmailExists() {
        customerRepository.save(CUSTOMER);

        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(CUSTOMER));
    }

    @Test
    public void saveThrowsDuplicateEntryIfIdExists() {
        customerRepository.save(CUSTOMER);

        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(CUSTOMER));
    }

    @Test
    public void findByIdReturnsCustomer() {
        customerRepository.save(CUSTOMER);

        final var customerFromDatabase = customerRepository.findById(CUSTOMER.getId()).get();

        assertEquals(CUSTOMER, customerFromDatabase);
    }

    @Test
    public void findByIdReturnsEmptyOptional() {
        final var maybeCustomer = customerRepository.findById(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, maybeCustomer::get);
    }

    @Test
    public void findByEmailReturnsCustomer() {
        customerRepository.save(CUSTOMER);

        final var customerFromDatabase = customerRepository.findByEmail(CUSTOMER.getEmail()).get();

        assertEquals(CUSTOMER, customerFromDatabase);
    }

    @Test
    public void findByEmailReturnsEmptyOptional() {
        final var maybeCustomer = customerRepository.findByEmail("nothing@else.matters");

        assertThrows(NoSuchElementException.class, maybeCustomer::get);
    }

    @Test
    public void findAllReturnsFullyList() {
        final var customerList = List.of(
            CustomerTestData.aCustomer()
                .email("1").build(),
            CustomerTestData.aCustomer()
                .email("2").build(),
            CustomerTestData.aCustomer()
                .email("3").build(),
            CustomerTestData.aCustomer()
                .email("4").build()
        );
        customerList.forEach(customerRepository::save);

        final var customerListFromDB = customerRepository.findAll();

        assertEquals(customerList, customerListFromDB);
    }

    @Test
    public void updatesCustomerInDB() {
        customerRepository.save(CUSTOMER);
        final var newName = "newName";
        final var updatedCustomer = CUSTOMER.toBuilder().name(newName).build();

        customerRepository.update(updatedCustomer);
        final var customerFromDB = customerRepository.findById(updatedCustomer.getId()).get();

        assertEquals(newName, customerFromDB.getName());
    }

    @Test
    public void updatesSetUpdateTime() {
        Customer savedCustomer = customerRepository.save(CUSTOMER);
        Customer updatedCustomer = customerRepository.update(savedCustomer.toBuilder().updateTime(null).build());

        assertNotNull(updatedCustomer.getUpdateTime());
    }

    @Test
    public void deletesCustomerFromDB() {
        customerRepository.save(CUSTOMER);

        boolean result = customerRepository.delete(CUSTOMER.getId());

        assertTrue(result);
    }
}
