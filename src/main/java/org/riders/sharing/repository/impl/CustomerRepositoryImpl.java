package org.riders.sharing.repository.impl;

import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.utils.ErrorMessages;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.riders.sharing.utils.ErrorMessages.CUSTOMER_DUPLICATE;
import static org.riders.sharing.utils.SqlUtils.DUPLICATE_ENTRY_SQL_ERR_CODE;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final ConnectionPool connectionPool;

    public CustomerRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            SELECT * FROM customers
            WHERE email = ?""")) {
            preparedStatement.setString(1, email);
            final var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Customer.customerFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find by email %s".formatted(email), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Customer save(Customer customer) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            INSERT INTO customers(
                id,
                create_time,
                update_time,
                name, surname,
                email,
                password
            )
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """)) {
            final var customerToStore = customer.toBuilder()
                .createTime(Instant.now())
                .updateTime(Instant.now())
                .build();

            preparedStatement.setObject(1, customerToStore.getId(), Types.OTHER);
            preparedStatement.setTimestamp(2, Timestamp.from(customerToStore.getCreateTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(customerToStore.getUpdateTime()));
            preparedStatement.setString(4, customerToStore.getName());
            preparedStatement.setString(5, customerToStore.getSurname());
            preparedStatement.setString(6, customerToStore.getEmail());
            preparedStatement.setString(7, customerToStore.getPassword());

            preparedStatement.executeUpdate();

            return customerToStore;
        } catch (SQLException e) {
            if (e.getSQLState().equals(DUPLICATE_ENTRY_SQL_ERR_CODE)) {
                throw new DuplicateEntryException(
                    CUSTOMER_DUPLICATE.formatted(customer.getId()), e);
            }

            throw new DatabaseException(
                "Error occurred when trying to save customer with id %s".formatted(customer.getId()), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Customer update(Customer customer) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            UPDATE customers
            SET update_time = ?,
            name = ?,
            surname = ?,
            email = ?,
            password = ?
            WHERE id = ?;""")) {
            Customer customerToStore = customer.toBuilder()
                .updateTime(Instant.now())
                .build();

            preparedStatement.setTimestamp(1, Timestamp.from(customerToStore.getUpdateTime()));
            preparedStatement.setString(2, customerToStore.getName());
            preparedStatement.setString(3, customerToStore.getSurname());
            preparedStatement.setString(4, customerToStore.getEmail());
            preparedStatement.setString(5, customerToStore.getPassword());
            preparedStatement.setObject(6, customerToStore.getId(), Types.OTHER);

            preparedStatement.executeUpdate();

            return customerToStore;
        } catch (SQLException e) {
            throw new DatabaseException(
                "Error occurred when trying to update customer with id %s".formatted(customer.getId()), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement(
            "SELECT * FROM customers WHERE id = ?")) {
            preparedStatement.setObject(1, id, Types.OTHER);

            final var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Customer.customerFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find customer by id %s".formatted(id), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Customer> findAll() {
        final var customerList = new ArrayList<Customer>();
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement(
            "SELECT * FROM customers;")) {
            final var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Customer customer = Customer.customerFromResultSet(resultSet);
                customerList.add(customer);
            }

            return customerList;
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to find all customers", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement(
            "DELETE FROM customers WHERE id = ?")) {
            preparedStatement.setObject(1, id, Types.OTHER);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error occurred when trying to delete user with %s".formatted(id), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
