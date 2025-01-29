package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPull;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.factory.impl.CustomerFactoryImpl;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.utils.constants.CustomerSqlColumns;
import org.riders.sharing.utils.constants.CustomerSqlQueries;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final CustomerFactory customerFactory = new CustomerFactoryImpl();
    private final ConnectionPull connectionPull = ConnectionPull.getInstance();

    @Override
    public void saveCustomer(Customer customer) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.INSERT_QUERY);

            int id = customer.getCustomerId();
            String name = customer.getName();
            String surname = customer.getSurname();
            String email = customer.getEmail();
            String passwordHash = customer.getPasswordHash();

            statement.setInt(CustomerSqlColumns.CUSTOMER_ID, id);
            statement.setString(CustomerSqlColumns.NAME, name);
            statement.setString(CustomerSqlColumns.SURNAME, surname);
            statement.setString(CustomerSqlColumns.EMAIL, email);
            statement.setString(CustomerSqlColumns.PASSWORD_HASH, passwordHash);

            statement.execute();
        } catch (SQLException e) {
            logger.error("Can't save customer: " + customer, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void changeCustomerName(int customerId, String name) {

    }

    @Override
    public void changeCustomerSurname(int customerId, String surname) {

    }

    @Override
    public void changeCustomerEmail(int customerId, String email) {

    }

    @Override
    public void changeCustomerPassword(int customerId, String password) {

    }

    @Override
    public Optional<Customer> findCustomerById(int id) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.FIND_USER_BY_ID);
            statement.setInt(CustomerSqlColumns.CUSTOMER_ID, id);
            ResultSet resultSet = statement.executeQuery();
            return customerFactory.createCustomerFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return null;
    }

    @Override
    public void deleteCustomer(Customer customer) {

    }
}
