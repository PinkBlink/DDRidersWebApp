package org.riders.sharing.factory.impl;

import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.model.Customer;
import org.riders.sharing.utils.constants.CustomerSqlColumns;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerFactoryImpl implements CustomerFactory {

    @Override
    public Customer createCustomer(int customerId, String name, String surname, String email, String passwordHash) {
        return new Customer(customerId, name, surname, email, passwordHash);
    }

    @Override
    public Customer createCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        int customerId = resultSet.getInt(CustomerSqlColumns.CUSTOMER_ID.getName());
        String name = resultSet.getString(CustomerSqlColumns.NAME.getName());
        String surname = resultSet.getString(CustomerSqlColumns.SURNAME.getName());
        String email = resultSet.getString(CustomerSqlColumns.EMAIL.getName());
        String passwordHash = resultSet.getString(CustomerSqlColumns.PASSWORD_HASH.getName());

        return createCustomer(
                customerId
                , name
                , surname
                , email
                , passwordHash);
    }
}