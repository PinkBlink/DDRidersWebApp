import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerRepositoryTest extends BaseTest implements CustomerTestData {

    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);

    @Test
    public void savesCustomerToDb() {
        final var customer = aCustomer().build();

        final var savedCustomer = customerRepository.save(customer);

        final var customerFromDb = customerRepository.findById(savedCustomer.getId()).get();
        assertEquals(savedCustomer, customerFromDb);
        assertNotNull(savedCustomer.getUpdateTime());
        assertNotNull(savedCustomer.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfEmailExists() {
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void saveThrowsDuplicateEntryIfIdExists() {
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void findByIdReturnsCustomer() {
        final var customer = aCustomer().build();
        customerRepository.save(customer);

        final var customerFromDb = customerRepository.findById(customer.getId()).get();

        assertEquals(customer, customerFromDb);
    }

    @Test
    public void findByEmailReturnsCustomer() {
        final var customer = aCustomer().build();
        customerRepository.save(customer);

        final var customerFromDb = customerRepository.findByEmail(customer.getEmail()).get();

        assertEquals(customer, customerFromDb);
    }

    @Test
    public void findAllReturnsFullyList() {
        final var customerList = List.of(
            aCustomer()
                .email("1").build(),
            aCustomer()
                .email("2").build()
        );
        customerList.forEach(customerRepository::save);

        final var customerListFromDB = customerRepository.findAll();

        assertEquals(customerList, customerListFromDB);
    }

    @Test
    public void updatesCustomerInDb() {
        final var customer = aCustomer().build();
        customerRepository.save(customer);
        final var newName = "newName";

        final var updatedCustomer = customer.toBuilder()
            .updateTime(null)
            .name(newName).build();
        customerRepository.update(updatedCustomer);
        final var customerFromDB = customerRepository.findById(updatedCustomer.getId()).get();

        assertEquals(updatedCustomer, customerFromDB);
        assertNotNull(customerFromDB.getUpdateTime());
    }

    @Test
    public void deletesCustomerFromDb() {
        final var customer = aCustomer().build();
        customerRepository.save(customer);

        final var result = customerRepository.delete(customer.getId());

        assertTrue(result);
    }
}
