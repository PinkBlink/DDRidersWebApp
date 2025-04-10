import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public interface OrderTestData extends CustomerTestData, ScooterTestData {
    default Order.Builder anOrder() {
        final var scooterRepo = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
        final var customerRepo = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
        final var scooter = aScooter().build();
        final var customer = aCustomer().build();
        final var savedScooter = scooterRepo.save(scooter);
        customerRepo.save(customer);

        return Order.Builder.order()
            .scooter(savedScooter)
            .startTime(Instant.now())
            .customerId(customer.getId())
            .status(OrderStatus.ONGOING);
    }
}
