import org.riders.sharing.model.Customer;
import org.riders.sharing.utils.PasswordEncryptor;

import java.util.UUID;

public interface CustomerTestData {
    default Customer.Builder aCustomer() {
        return Customer.Builder.customer()
            .name("John")
            .surname("Travolta")
            .email(UUID.randomUUID().toString())
            .password(PasswordEncryptor.encryptPassword("password"));
    }
}
