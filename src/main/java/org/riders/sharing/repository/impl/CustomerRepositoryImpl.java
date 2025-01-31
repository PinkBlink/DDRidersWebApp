package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPull;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.factory.impl.CustomerFactoryImpl;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.utils.constants.CustomerSqlQueries;

import java.sql.*;
import java.util.ArrayList;
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

            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, surname);
            statement.setString(4, email);
            statement.setString(5, passwordHash);

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error occurred while trying to save customer: " + customer, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            int id = customer.getCustomerId();
            String name = customer.getName();
            String surname = customer.getSurname();
            String email = customer.getEmail();
            String passwordHash = customer.getPasswordHash();

            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.UPDATE_CUSTOMER_FIELDS);
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, email);
            statement.setString(4, passwordHash);
            statement.setInt(5, id);

            int result = statement.executeUpdate();
            if (result > 0) {
                logger.info("Customer with id " + id + " updated successfully;");
            } else {
                logger.info("Couldn't find customer with id " + id);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to update customer");
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Customer> findCustomerById(int customerId) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.FIND_USER_BY_ID);
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                logger.info("Successfully found customer with customerId: " + customerId);
                return Optional.of(customerFactory.createCustomerFromResultSet(resultSet));
            }
            logger.info("Can't find customer with id: " + customerId);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error occurred while attempting to find customer with id: " + customerId);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Customer> findAll() throws RepositoryException {
        List<Customer> customerList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.FIND_ALL_CUSTOMERS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Customer customer = customerFactory.createCustomerFromResultSet(resultSet);
                customerList.add(customer);
            }
            if (customerList.isEmpty()) {
                logger.info("Customer list is empty;");
            } else {
                logger.info("Successfully find " + customerList.size() + " customer(s)");
            }
            return customerList;
        } catch (SQLException e) {
            logger.error("Error occurred while attempting to find all customers.");
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void deleteCustomer(int customerId) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.DELETE_CUSTOMER_BY_ID);
            statement.setInt(1, customerId);
            int result = statement.executeUpdate();
            if (result > 0) {
                logger.info("Customer with id :" + customerId + " successfully deleted");
            } else {
                logger.info("Can't find customer with id: " + customerId);
            }
        } catch (SQLException e) {
            logger.error("Error while deleting customer with id=" + customerId, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
