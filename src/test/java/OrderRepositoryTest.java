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
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder().customerId(customer.getId()).scooter(scooter).build();

        //when
        final var savedOrder = orderRepository.save(order);
        final var orderFromDb = orderRepository.findById(order.getId()).get();

        //then
        assertEquals(savedOrder, orderFromDb);
        assertNotNull(orderFromDb.getUpdateTime());
        assertNotNull(orderFromDb.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfAlreadyExists() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder()
            .customerId(customer.getId())
            .scooter(scooter)
            .build();

        orderRepository.save(order);

        //then
        assertThrows(DuplicateEntryException.class, () -> orderRepository.save(order));
    }

    @Test
    public void findByIdReturnsOrder() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder()
            .customerId(customer.getId())
            .scooter(scooter).build();

        orderRepository.save(order);

        //when
        final var orderFromDb = orderRepository.findById(order.getId()).get();

        //then
        assertEquals(order, orderFromDb);
    }

    @Test
    public void findByStatusReturnsFullyList() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var status = OrderStatus.ONGOING;

        final var orderList = List.of(
            anOrder()
                .customerId(customer.getId())
                .scooter(scooter)
                .build(),
            anOrder()
                .customerId(customer.getId())
                .scooter(scooter)
                .build()
        );
        orderList.forEach(orderRepository::save);

        //when
        final var orderListFromDb = orderRepository.findOrdersByStatus(status);

        //then
        assertEquals(orderList, orderListFromDb);
    }

    @Test
    public void findCustomerOrdersReturnsFullyList() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());

        final var completedOrderList = List.of(
            anOrder()
                .customerId(customer.getId())
                .status(OrderStatus.COMPLETED)
                .scooter(scooter)
                .build(),
            anOrder()
                .customerId(customer.getId())
                .status(OrderStatus.COMPLETED)
                .scooter(scooter)
                .build()
        );
        completedOrderList.forEach(orderRepository::save);

        //when
        final var orderListFromDb = orderRepository.findCustomerOrdersByStatus(customer.getId(), OrderStatus.COMPLETED);

        //then
        assertEquals(completedOrderList, orderListFromDb);
    }


    @Test
    public void findAllReturnsFullyList() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var orderList = List.of(
            anOrder()
                .customerId(customer.getId())
                .scooter(scooter)
                .build(),
            anOrder()
                .customerId(customer.getId())
                .scooter(scooter)
                .build()
        );
        orderList.forEach(orderRepository::save);

        //when
        final var orderListFromDb = orderRepository.findAll();

        //then
        assertEquals(orderList, orderListFromDb);
    }

    @Test
    public void updatesOrderInDb() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder()
            .customerId(customer.getId())
            .scooter(scooter)
            .build();
        orderRepository.save(order);

        //when
        final var updatedOrder = order.toBuilder()
            .status(OrderStatus.COMPLETED)
            .updateTime(null)
            .build();
        orderRepository.update(updatedOrder);

        final var orderFromDb = orderRepository.findById(updatedOrder.getId()).get();

        //then
        assertEquals(updatedOrder, orderFromDb);
        assertNotNull(orderFromDb.getUpdateTime());
    }

    @Test
    public void deletesOrderFromDB() {
        //given
        final var scooter = scooterRepository.save(aScooter().build());
        final var customer = customerRepository.save(aCustomer().build());
        final var order = anOrder()
            .customerId(customer.getId())
            .scooter(scooter)
            .build();
        orderRepository.save(order);

        //when
        final var result = orderRepository.delete(order.getId());

        //then
        assertTrue(result);
    }
}
