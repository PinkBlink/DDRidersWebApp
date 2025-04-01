package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final ConnectionPool connectionPool = ConnectionPool.INSTANCE;

    @Override
    public Optional<Customer> findByEmail(String email) {
        Connection connection = connectionPool.getConnection();
        PreparedStatement preparedStatement = null;
        Optional<Customer> maybeCustomer = Optional.empty();

        try {
            preparedStatement = connection.prepareStatement("""
                    SELECT * FROM customers
                    WHERE email = ?""");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                maybeCustomer = Optional.of(Customer.createCustomerFromResultSet(resultSet));
                logger.info("Customer with email {} was successfully found.", email);
            } else {
                logger.warn("Couldn't find customer with email {}", email);
            }
            return maybeCustomer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(preparedStatement);
        }
    }

    @Override
    public Customer save(Customer customer) {
        Connection connection = connectionPool.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("""
                    INSERT INTO customers(id, create_time, update_time, name, surname, email, password)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """);
            Customer customerToStore = customer.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            preparedStatement.setObject(1, customerToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(customerToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(customerToStore.getUpdateTime()));
            preparedStatement.setString(4, customerToStore.getName());
            preparedStatement.setString(5, customerToStore.getSurname());
            preparedStatement.setString(6, customerToStore.getEmail());
            preparedStatement.setString(7, customerToStore.getPassword());

            boolean result = preparedStatement.execute();
            if (result) {
                logger.info("Customer {} was successfully saved", customerToStore);
                return customerToStore;
            } else {
                logger.error("Couldn't save customer {}", customer);
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(preparedStatement);
        }
    }

    @Override
    public Customer update(Customer entity) {
        return null;
    }

    @Override
    public Optional<Customer> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return List.of();
    }

    @Override
    public boolean isExist(Customer entity) {
        return false;
    }

    @Override
    public Customer delete(UUID id) {
        return null;
    }
}
