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
        //given
        final var customer = aCustomer().build();

        //when
        final var savedCustomer = customerRepository.save(customer);
        final var customerFromDb = customerRepository.findById(savedCustomer.getId()).get();

        //then
        assertEquals(savedCustomer, customerFromDb);
        assertNotNull(savedCustomer.getUpdateTime());
        assertNotNull(savedCustomer.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfEmailExists() {
        //given
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        //then
        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void saveThrowsDuplicateEntryIfIdExists() {
        //given
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        //then
        assertThrows(DuplicateEntryException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void findByIdReturnsCustomer() {
        //given
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        //when
        final var customerFromDb = customerRepository.findById(customer.getId()).get();

        //then
        assertEquals(customer, customerFromDb);
    }

    @Test
    public void findByEmailReturnsCustomer() {
        //given
        final var customer = aCustomer().build();

        customerRepository.save(customer);

        //when
        final var customerFromDb = customerRepository.findByEmail(customer.getEmail()).get();

        //then
        assertEquals(customer, customerFromDb);
    }

    @Test
    public void findAllReturnsFullyList() {
        //given
        final var customerList = List.of(
            aCustomer()
                .email("1")
                .build(),
            aCustomer()
                .email("2")
                .build()
        );
        customerList.forEach(customerRepository::save);

        //find
        final var customerListFromDB = customerRepository.findAll();

        //then
        assertEquals(customerList, customerListFromDB);
    }

    @Test
    public void updatesCustomerInDb() {
        //given
        final var customer = aCustomer().build();
        final var newName = "newName";

        customerRepository.save(customer);

        final var updatedCustomer = customer.toBuilder()
            .updateTime(null)
            .name(newName)
            .build();

        //when
        customerRepository.update(updatedCustomer);
        final var customerFromDB = customerRepository.findById(updatedCustomer.getId()).get();

        //then
        assertEquals(updatedCustomer, customerFromDB);
        assertNotNull(customerFromDB.getUpdateTime());
    }

    @Test
    public void deletesCustomerFromDb() {
        //given
        final var customer = aCustomer().build();
        customerRepository.save(customer);

        //when
        final var result = customerRepository.delete(customer.getId());

        //then
        assertTrue(result);
    }
}
