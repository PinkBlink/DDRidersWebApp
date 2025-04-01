package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.*;
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
    public Optional<Customer> findByEmail(String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement("""
                    SELECT * FROM customers
                    WHERE email = ?""");

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                logger.info("Customer with email {} was successfully found.", email);

                return Optional.of(Customer.createCustomerFromResultSet(resultSet));

            } else {
                logger.warn("Couldn't find customer with email {}", email);
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find customer by email {}", email, e);
            throw new NoSQLConnectionException(e.getMessage());
        } finally {
            closeStatement(preparedStatement);
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Customer save(Customer customer) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Customer customerToStore = customer.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement("""
                    INSERT INTO customers(id, create_time, update_time, name, surname, email, password)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """);


            preparedStatement.setObject(1, customerToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(customerToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(customerToStore.getUpdateTime()));
            preparedStatement.setString(4, customerToStore.getName());
            preparedStatement.setString(5, customerToStore.getSurname());
            preparedStatement.setString(6, customerToStore.getEmail());
            preparedStatement.setString(7, customerToStore.getPassword());

            preparedStatement.executeUpdate();
            logger.info("Successfully saved customer: {}", customer);

            return customerToStore;

        } catch (SQLException e) {
            logger.error("Customer with email or id is already exist customer: {}", customer, e);
            throw new CustomerExistsException(e.getMessage());
        } finally {
            closeStatement(preparedStatement);
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Customer update(Customer customer) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Customer customerToStore = customer.toBuilder()
                    .setUpdateTime(Instant.now()
                    ).build();


            connection = connectionPool.getConnection();
            statement = connection.prepareStatement("""
                    UPDATE customers
                    SET update_time = ?,
                    name = ?,
                    surname = ?,
                    email = ?,
                    password = ?
                    WHERE id = ?;""");

            statement.setTimestamp(1, Timestamp.from(customerToStore.getUpdateTime()));
            statement.setString(2, customerToStore.getName());
            statement.setString(3, customerToStore.getSurname());
            statement.setString(4, customerToStore.getEmail());
            statement.setString(5, customerToStore.getPassword());
            statement.setObject(6, customerToStore.getId(), Types.OTHER);

            boolean success = statement.executeUpdate() > 0;

            if (success) {
                logger.info("Customer updated successfully. Customer: {}", customerToStore.getId());
            } else {
                logger.warn("Couldn't find customer: {}", customerToStore.getId());
            }

            return customerToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to update customer: {}", customer);
            throw new BadDatabaseUpdateException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Customer> find(UUID id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?");

            statement.setObject(1, id, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                logger.info("Successfully found customer with customerId: {}", id);
                return Optional.of(Customer.createCustomerFromResultSet(resultSet));
            }

            logger.warn("Can't find customer with id: {}", id);

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while attempting to find customer with id: {}", id);
            throw new BadDatabaseSelectException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Customer> findAll() {
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
                logger.info("Successfully find {} customer(s)", customerList.size());
            }

            return customerList;

        } catch (SQLException e) {
            logger.error("Error occurred while attempting to find all customers.");
            throw new BadDatabaseSelectException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean isExist(Customer customer) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement("""
                    SELECT * FROM customers
                    WHERE email = ?""");

            statement.setObject(1, customer.getEmail(), Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to check the existing customer with email {}"
                    , customer.getEmail());
            throw new BadDatabaseSelectException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean delete(UUID id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement("DELETE FROM customers WHERE id = ?");
            statement.setObject(1, id, Types.OTHER);

            boolean result = statement.executeUpdate() > 0;

            if (result) {
                logger.info("Customer with id: {} successfully deleted", id);
            } else {
                logger.info("Can't find customer with id: {}", id);
            }

            return result;

        } catch (SQLException e) {
            logger.error("Error while deleting customer with id: {}", id, e);
            throw new BadDatabaseUpdateException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
