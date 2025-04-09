import org.riders.sharing.model.Customer;

public interface CustomerTestData {
    default Customer.Builder aCustomer() {
        return Customer.Builder.customer()
            .name("name")
            .surname("surname")
            .email("email@email.com")
            .password("pass");
    }
}
