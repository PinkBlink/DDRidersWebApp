import org.junit.jupiter.api.*;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateIdOrEmailException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class CustomerRepositoryTests {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl();

    private static Customer validCustomer1 = Customer.Builder.getNewBuilderWithId()
            .setName("Valid1").setSurname("surname")
            .setEmail("test@test.com")
            .setPassword("test")
            .build();

    private static final Customer validCustomer2 = Customer.Builder.getNewBuilderWithId()
            .setName("Valid2")
            .setSurname("surname2")
            .setEmail("email2@email.com")
            .setPassword("test")
            .build();

    @BeforeAll
    public static void beforeAll() throws SQLException {
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }

    @AfterAll
    public static void afterAll() throws SQLException {
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }

    @Test
    public void saveShouldSetCreateAndUpdateTime() {
        Customer savedCustomer = customerRepository.save(validCustomer1);

        Assertions.assertTrue(savedCustomer.getCreateTime().equals(savedCustomer.getUpdateTime())
                && validCustomer1.equals(savedCustomer));
    }

    @Test
    public void saveShouldThrowIfEqualsEmails() {
        customerRepository.save(validCustomer1);

        Assertions.assertThrows(DuplicateIdOrEmailException.class, () -> customerRepository.save(validCustomer1));
    }

    @Test
    public void saveShouldThrowIfEqualsIDs() {
        customerRepository.save(validCustomer1);

        Assertions.assertThrows(DuplicateIdOrEmailException.class, () -> customerRepository.save(validCustomer1));
    }

    @Test
    public void findShouldReturnCustomer() {
        customerRepository.save(validCustomer1);

        Customer customerFromDatabase = customerRepository.findById(validCustomer1.getId()).get();

        Assertions.assertEquals(validCustomer1, customerFromDatabase);
    }

    @Test
    public void findShouldReturnEmptyOptional() {
        Optional<Customer> maybeCustomer = customerRepository.findById(UUID.randomUUID());

        Assertions.assertThrows(NoSuchElementException.class, maybeCustomer::get);
    }

    @Test
    public void findByEmailCustomerShouldReturnCustomer() {
        customerRepository.save(validCustomer1);

        Customer customerFromDatabase = customerRepository.findByEmail(validCustomer1.getEmail()).get();

        Assertions.assertEquals(validCustomer1, customerFromDatabase);
    }

    @Test
    public void findByEmailShouldReturnEmptyOptional() {
        Optional<Customer> maybeCustomer = customerRepository.findByEmail("nothing@else.matters");

        Assertions.assertThrows(NoSuchElementException.class, maybeCustomer::get);
    }

    @Test
    public void findAllShouldReturnAll() {
        customerRepository.save(validCustomer1);
        customerRepository.save(validCustomer2);

        List<Customer> customers = customerRepository.findAll();

        Assertions.assertEquals(2, customers.size());
    }

    @Test
    public void updateShouldReturnUpdatedCustomer() {
        String newName = "newName";
        validCustomer1 = customerRepository.update(validCustomer1.toBuilder().setName(newName).build());


        Assertions.assertEquals(newName, validCustomer1.getName());
    }

    @Test
    public void updateShouldSetUpdateTime() throws InterruptedException {
        Customer savedCustomer = customerRepository.save(validCustomer1);

        Thread.sleep(10);

        Customer updatedCustomer = customerRepository.update(savedCustomer);
        String errorMessage = "Saved: %s  Updated: %s "
                .formatted(savedCustomer.getUpdateTime(), updatedCustomer.getUpdateTime());

        Assertions.assertTrue(savedCustomer.getUpdateTime().isBefore(updatedCustomer.getUpdateTime()), errorMessage);
    }

    @Test
    public void updateShouldThrow() {
        customerRepository.save(validCustomer1);
        customerRepository.save(validCustomer2);

        Customer updatedCustomerWithSameEmail = validCustomer2.toBuilder().setEmail(validCustomer1.getEmail()).build();

        Assertions.assertThrows(DatabaseException.class,
                () -> customerRepository.update(updatedCustomerWithSameEmail));
    }

    @Test
    public void deleteShouldReturnTrue() {
        customerRepository.save(validCustomer1);

        boolean result = customerRepository.delete(validCustomer1.getId());

        Assertions.assertTrue(result);
    }

    @Test
    public void deleteShouldReturnFalse() {
        boolean result = customerRepository.delete(validCustomer2.getId());

        Assertions.assertFalse(result);
    }

    private static void deleteCustomersFromDatabase(Customer... customers) throws SQLException {
        for (Customer customer : customers) {
            deleteCustomerFromDatabase(customer);
        }
    }

    private static void deleteCustomerFromDatabase(Customer customer) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ConnectionPool connectionPool = ConnectionPool.INSTANCE;
        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM customers WHERE id = ?");
            preparedStatement.setObject(1, customer.getId(), Types.OTHER);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Something goes wrong.", e);
        } finally {
            connectionPool.releaseConnection(connection);
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
