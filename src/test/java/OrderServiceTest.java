import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.service.impl.ScooterServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderServiceTest extends BaseTest implements OrderTestData, CustomerTestData, ScooterTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final OrderRepository orderRepository = new OrderRepositoryImpl(ConnectionPool.INSTANCE);

    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final OrderService orderService = new OrderServiceImpl(customerService, scooterService, orderRepository);

    @Test
    public void createsOrder() {
        //given
        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedScooter = scooterRepository.save(aScooter().build());

        final var createOrderDto = new CreateOrderDto(
            savedCustomer.getId().toString(),
            savedScooter.getId().toString()
        );

        //when
        final var createdOrder = orderService.createOrder(createOrderDto);
        final var orderFromDb = orderRepository.findById(createdOrder.getId()).get();

        //then
        assertEquals(createdOrder, orderFromDb);
    }

    @Test
    public void createOrderThrowsBadRequest() {
        //given
        final var badCreateOrderDto = new CreateOrderDto(null, null);

        //then
        assertThrows(BadRequestException.class, () -> orderService.createOrder(badCreateOrderDto));
    }
}
