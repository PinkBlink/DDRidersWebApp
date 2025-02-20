package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.exception.NoSQLConnectionException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerRepositoryImpl implements CustomerRepository {

    private final Logger logger = LogManager.getLogger(this);
    private final ConnectionPool connectionPool = ConnectionPool.INSTANCE;


    @Override
    public Customer save(Customer customer){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Customer customerToStore = customer.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            INSERT INTO customers(id, create_time, update_time, name, surname, email, password)
                            VALUES(?, ?, ?, ?, ?, ?, ?)
                             """
            );

            statement.setObject(1, customerToStore.getId(), Types.OTHER);
            statement.setTimestamp(2, Timestamp.from(customerToStore.getCreateTime()));
            statement.setTimestamp(3, Timestamp.from(customerToStore.getUpdateTime()));
            statement.setString(4, customerToStore.getName());
            statement.setString(5, customerToStore.getSurname());
            statement.setString(6, customerToStore.getEmail());
            statement.setString(6, customerToStore.getPassword());

            boolean result = statement.executeUpdate() > 0;
            logger.info((result)
                    ? "Customer with id " + customer.getId() + " is  successfully saved"
                    : "Couldn't customer with id: " + customer.getId());

            return customerToStore;
        } catch (SQLException e) {
            logger.error("Error occurred while trying to save customer: " + customer, e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Customer update(Customer customer){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Customer customerToStore = customer.toBuilder()
                    .setUpdateTime(Instant.now())
                    .build();


            connection = connectionPool.getConnection();
            statement = connection.prepareStatement("""
                    UPDATE customers
                    SET update_time = ?,
                    name = ?,
                    surname = ?,
                    email = ?,
                    password = ?,
                    WHERE id = ?;""");

            statement.setTimestamp(1, Timestamp.from(customerToStore.getUpdateTime()));
            statement.setString(2, customerToStore.getName());
            statement.setString(3, customerToStore.getSurname());
            statement.setString(4, customerToStore.getEmail());
            statement.setString(5, customerToStore.getPassword());
            statement.setObject(6, customerToStore.getId(), Types.OTHER);

            boolean success = statement.executeUpdate() > 0;
            if (success) {
                logger.info("Customer with id " + customerToStore.getId() + " updated successfully;");
            } else {
                logger.info("Couldn't find customer with id " + customerToStore.getId());
            }

            return customerToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to update customer");
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM customers WHERE customer_id = ?"
            );

            statement.setObject(1, customerId, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                logger.info("Successfully found customer with customerId: " + customerId);
                return Optional.of(Customer.createCustomerFromResultSet(resultSet));
            }

            logger.info("Can't find customer with id: " + customerId);

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while attempting to find customer with id: " + customerId);
            throw new NoSQLConnectionException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email){
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM customers WHERE email = ?"
            );

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Customer.createCustomerFromResultSet(resultSet));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Customer> findAll(){
        List<Customer> customerList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "SELECT * FROM customers;"
            );

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Customer customer = Customer.createCustomerFromResultSet(resultSet);
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
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean isExists(Customer customer) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement("""
                    SELECT * FROM customers
                    WHERE id = ?""");

            statement.setObject(1, customer.getId(), Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to check the existing customer with id %s"
                    .formatted(customer.getId()));
            throw new RuntimeException(e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean delete(UUID customerId){
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement("DELETE FROM customers WHERE customer_id = ?");
            statement.setObject(1, customerId, Types.OTHER);
            boolean result = statement.executeUpdate() > 0;

            if (result) {
                logger.info("Customer with id: " + customerId + " successfully deleted");
            } else {
                logger.info("Can't find customer with id: " + customerId);
            }

            return result;

        } catch (SQLException e) {
            logger.error("Error while deleting customer with id= " + customerId, e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
