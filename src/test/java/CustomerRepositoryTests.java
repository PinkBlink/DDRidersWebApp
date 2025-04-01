import org.junit.jupiter.api.*;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.BadDatabaseUpdateException;
import org.riders.sharing.exception.CustomerExistsException;
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
    public static void beforeAll(){
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }


    @AfterAll
    public static void afterAll(){
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }

    @AfterEach
    public void afterEach(){
        deleteCustomersFromDatabase(validCustomer1, validCustomer2);
    }

    @Test
    public void saveShouldSetCreateAndUpdateTime(){
        deleteCustomerFromDatabase(validCustomer1);

        Customer savedCustomer = customerRepository.save(validCustomer1);

        Assertions.assertTrue(savedCustomer.getCreateTime().equals(savedCustomer.getUpdateTime())
                && validCustomer1.equals(savedCustomer));
    }

    @Test
    public void saveShouldThrowIfEqualsEmails() {
        customerRepository.save(validCustomer1);

        Assertions.assertThrows(CustomerExistsException.class, () -> customerRepository.save(validCustomer1));
    }

    @Test
    public void saveShouldThrowIfEqualsIDs() {
        customerRepository.save(validCustomer1);

        Assertions.assertThrows(CustomerExistsException.class, () -> customerRepository.save(validCustomer1));
    }

    @Test
    public void findShouldReturnCustomer() {
        customerRepository.save(validCustomer1);

        Customer customerFromDatabase = customerRepository.find(validCustomer1.getId()).get();

        Assertions.assertEquals(validCustomer1, customerFromDatabase);
    }

    @Test
    public void findShouldReturnEmptyOptional() {
        Optional<Customer> maybeCustomer = customerRepository.find(UUID.randomUUID());

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
    public void updateShouldSetUpdateTime() {
        Customer savedCustomer = customerRepository.save(validCustomer1);

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

        Assertions.assertThrows(BadDatabaseUpdateException.class, () -> customerRepository.update(updatedCustomerWithSameEmail));
    }

    @Test
    public void isExistShouldReturnFalse() {
        Customer customer = Customer.Builder.getNewBuilderWithId().setEmail("null").setName("null").setSurname("null").setPassword("123").build();

        boolean result = customerRepository.isExist(customer);

        Assertions.assertFalse(result);
    }

    @Test
    public void isExistShouldReturnTrue() {
        customerRepository.save(validCustomer1);

        boolean result = customerRepository.isExist(validCustomer1);

        Assertions.assertTrue(result);
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

    private static void deleteCustomersFromDatabase(Customer... customers) {
        for (Customer customer : customers) {
            deleteCustomerFromDatabase(customer);
        }
    }

    private static void deleteCustomerFromDatabase(Customer customer) {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM customers WHERE id = ?")) {

            preparedStatement.setObject(1, customer.getId(), Types.OTHER);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new BadDatabaseUpdateException("Something goes wrong.");
        }
    }
}