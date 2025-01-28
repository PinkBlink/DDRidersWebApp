package org.riders.sharing.factory;

import org.riders.sharing.model.Customer;

public interface CustomerFactory {
    Customer createCustomer(int id, String name, String surname, String email, String passwordHash);
}
