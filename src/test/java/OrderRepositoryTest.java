import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderRepositoryTest extends BaseTest implements OrderTestData, ScooterTestData, CustomerTestData {
    private final OrderRepository orderRepository = new OrderRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);

    @Test
    public void saveOrderToDb() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();

        final var savedOrder = orderRepository.save(order);
        final var orderFromDb = orderRepository.findById(order.getId()).get();

        assertEquals(savedOrder, orderFromDb);
        assertNotNull(orderFromDb.getUpdateTime());
        assertNotNull(orderFromDb.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfAlreadyExists() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();

        orderRepository.save(order);

        assertThrows(DuplicateEntryException.class, () -> orderRepository.save(order));
    }

    @Test
    public void findByIdReturnsOrder() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();
        orderRepository.save(order);

        final var orderFromDb = orderRepository.findById(order.getId()).get();

        assertEquals(order, orderFromDb);
    }

    @Test
    public void findByStatusReturnsFullyList() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var status = OrderStatus.ONGOING;
        final var orderList = List.of(
            anOrder().customerId(customer.getId()).scooter(scooter).build(),
            anOrder().customerId(customer.getId()).scooter(scooter).build());
        orderList.forEach(orderRepository::save);

        final var orderListFromDb = orderRepository.findOrdersByStatus(status);

        assertEquals(orderList, orderListFromDb);
    }

    @Test
    public void findCustomerOrdersReturnsFullyList() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var completedOrderList = List.of(
            anOrder().customerId(customer.getId()).status(OrderStatus.COMPLETED).scooter(scooter).build(),
            anOrder().customerId(customer.getId()).status(OrderStatus.COMPLETED).scooter(scooter).build());
        completedOrderList.forEach(orderRepository::save);

        final var orderListFromDb = orderRepository.findCustomerOrdersByStatus(customer.getId(), OrderStatus.COMPLETED);

        assertEquals(completedOrderList, orderListFromDb);
    }


    @Test
    public void findAllReturnsFullyList() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var orderList = List.of(
            anOrder().customerId(customer.getId()).scooter(scooter).build(),
            anOrder().customerId(customer.getId()).scooter(scooter).build());
        orderList.forEach(orderRepository::save);

        final var orderListFromDb = orderRepository.findAll();

        assertEquals(orderList, orderListFromDb);
    }

    @Test
    public void updatesOrderInDb() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();
        orderRepository.save(order);

        final var updatedOrder = order.toBuilder().status(OrderStatus.COMPLETED).updateTime(null).build();
        orderRepository.update(updatedOrder);
        final var orderFromDb = orderRepository.findById(updatedOrder.getId()).get();

        assertEquals(updatedOrder, orderFromDb);
        assertNotNull(orderFromDb.getUpdateTime());
    }

    @Test
    public void deletesOrderFromDB() {
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();
        orderRepository.save(order);

        final var result = orderRepository.delete(order.getId());

        assertTrue(result);
    }
}
