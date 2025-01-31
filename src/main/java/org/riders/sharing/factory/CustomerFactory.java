package org.riders.sharing.factory;

import org.riders.sharing.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CustomerFactory {
    Customer createCustomer(int id, String name, String surname, String email, String passwordHash);
    Customer createCustomerFromResultSet(ResultSet resultSet) throws SQLException;
}
