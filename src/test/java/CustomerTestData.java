import org.riders.sharing.model.Customer;

public class CustomerTestData {
    public static Customer.Builder aCustomer(){
        return Customer.Builder.customer()
            .name("name")
            .surname("surname")
            .email("email@email.com")
            .password("pass");
    }
}
