import org.riders.sharing.model.Customer;

import java.util.UUID;

public interface CustomerTestData {
    default Customer.Builder aCustomer() {
        return Customer.Builder.customer()
            .name("name")
            .surname("surname")
            .email(UUID.randomUUID().toString())
            .password("123");
    }
}
