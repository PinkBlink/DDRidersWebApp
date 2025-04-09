package org.riders.sharing.repository.impl;

import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.OrderRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository {
    private final ConnectionPool connectionPool;

    public OrderRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Order save(Order order) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            INSERT INTO orders(id, create_time, update_time, customer_id, scooter_id, start_time, end_time, order_status)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?)""")) {
            final var orderToStore = order.toBuilder()
                .createTime(Instant.now())
                .updateTime(Instant.now())
                .build();

            preparedStatement.setObject(1, orderToStore.getId());
            preparedStatement.setTimestamp(2, Timestamp.from(orderToStore.getStartTime()));
            preparedStatement.setTimestamp(3, Timestamp.from(orderToStore.getUpdateTime()));
            preparedStatement.setObject(4, order.getCustomerId(), Types.OTHER);
            preparedStatement.setObject(5, order.getScooter().getId(), Types.OTHER);
            preparedStatement.setTimestamp(6, Timestamp.from(orderToStore.getStartTime()));
            preparedStatement.setTimestamp(7, (orderToStore.getEndTime() == null)
                ? null
                : Timestamp.from(orderToStore.getEndTime()));

            preparedStatement.setObject(8, order.getStatus(), Types.OTHER);

            return orderToStore;
        } catch (SQLException e) {
            throw new DuplicateEntryException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Order update(Order order) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            UPDATE orders
            SET update_time = ?,
            customer_id = ?,
            scooter_id = ?,
            start_time = ?,
            end_time = ?,
            order_status = ?
            WHERE id = ?;""")) {
            final var orderToStore = order.toBuilder()
                .updateTime(Instant.now())
                .build();

            preparedStatement.setTimestamp(1, Timestamp.from(orderToStore.getUpdateTime()));
            preparedStatement.setObject(2, orderToStore.getCustomerId());
            preparedStatement.setObject(3, orderToStore.getScooter().getId());
            preparedStatement.setTimestamp(4, Timestamp.from(orderToStore.getStartTime()));
            preparedStatement.setTimestamp(5, (orderToStore.getEndTime() == null)
                ? null
                : Timestamp.from(orderToStore.getEndTime()));

            preparedStatement.setObject(6, orderToStore.getStatus(), Types.OTHER);
            preparedStatement.setObject(7, orderToStore.getId(), Types.OTHER);

            return orderToStore;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            SELECT * FROM orders
            JOIN scooters ON scooter_id = scooters.id
            WHERE orders.id = ?""")) {
            preparedStatement.setObject(1, id, Types.OTHER);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Order.orderFromResultSet(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Order> findAll() {
        final var connection = connectionPool.getConnection();
        final var orderList = new ArrayList<Order>();

        try (final var preparedStatement = connection.prepareStatement("""
            SELECT * FROM orders
            JOIN scooters ON scooter_id = scooters.id""")) {
            final var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = Order.orderFromResultSet(resultSet);
                orderList.add(order);
            }

            return orderList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(UUID id) {
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement("""
            DELETE FROM orders
            WHERE id = ?""")) {
            preparedStatement.setObject(1, id, Types.OTHER);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Order> findCustomerOrdersByStatus(UUID customerId, OrderStatus status) {
        final var connection = connectionPool.getConnection();
        final var orderList = new ArrayList<Order>();

        try (final var preparedStatement = connection.prepareStatement("""
            SELECT * FROM orders
            JOIN scooters ON scooter_id = scooters.id
            WHERE customer_id = ?
            AND order_status = ?;""")) {
            preparedStatement.setObject(1, customerId, Types.OTHER);
            preparedStatement.setObject(2, status, Types.OTHER);
            final var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final var order = Order.orderFromResultSet(resultSet);
                orderList.add(order);
            }

            return orderList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus orderStatus) {
        final var connection = connectionPool.getConnection();
        final var orderList = new ArrayList<Order>();

        try (final var preparedStatement = connection.prepareStatement("""
            SELECT * FROM orders
            JOIN scooters ON scooter_id = scooters.id
            WHERE order_status = ?""")) {
            preparedStatement.setObject(1, orderStatus, Types.OTHER);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = Order.orderFromResultSet(resultSet);
                orderList.add(order);
            }

            return orderList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
