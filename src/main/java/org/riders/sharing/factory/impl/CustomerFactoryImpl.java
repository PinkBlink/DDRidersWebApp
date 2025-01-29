package org.riders.sharing.factory.impl;

import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.model.Customer;
import org.riders.sharing.utils.constants.CustomerSqlColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerFactoryImpl implements CustomerFactory {

    @Override
    public Customer createCustomer(int customerId, String name, String surname, String email, String passwordHash) {
        return new Customer(customerId, name, surname, email, passwordHash);
    }

    @Override
    public Optional<Customer> createCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            int customerId = resultSet.getInt(CustomerSqlColumns.CUSTOMER_ID);
            String name = resultSet.getString(CustomerSqlColumns.NAME);
            String surname = resultSet.getString(CustomerSqlColumns.SURNAME);
            String email = resultSet.getString(CustomerSqlColumns.EMAIL);
            String passwordHash = resultSet.getString(CustomerSqlColumns.PASSWORD_HASH);

            return Optional.of(createCustomer(
                    customerId
                    , name
                    , surname
                    , email
                    , passwordHash)
            );
        }
        return Optional.empty();
    }
}