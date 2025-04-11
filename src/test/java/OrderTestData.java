import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;

import java.time.Instant;

public interface OrderTestData {
    default Order.Builder anOrder() {
        return Order.Builder.order()
            .startTime(Instant.now())
            .status(OrderStatus.ONGOING);
    }
}
