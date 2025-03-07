package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.OrderRepository;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class OrderRepositoryImpl implements OrderRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final ConnectionPool connectionPool = ConnectionPool.INSTANCE;

    @Override
    public Order save(Order order) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Order orderToStore = order.toBuilder()
                    .setCreateTime(Instant.now())
                    .setUpdateTime(Instant.now())
                    .build();

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    "INSERT INTO orders(id, create_time, update_time, customer_id, " +
                            "scooter_id, start_time, end_time, order_status) " +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setObject(1, orderToStore.getId());
            statement.setTimestamp(2, Timestamp.from(orderToStore.getStartTime()));
            statement.setTimestamp(3, Timestamp.from(orderToStore.getUpdateTime()));
            statement.setObject(4, order.getCustomerId(), Types.OTHER);
            statement.setObject(5, order.getScooter().getId(), Types.OTHER);
            statement.setTimestamp(6, Timestamp.from(orderToStore.getStartTime()));

            statement.setTimestamp(7, (orderToStore.getEndTime() == null)
                    ? null
                    : Timestamp.from(orderToStore.getEndTime()));

            statement.setObject(8, order.getOrderStatus(), Types.OTHER);

            boolean isSuccess = statement.executeUpdate() > 0;
            if (isSuccess) {
                logger.info("Order saved successfully; " + order);
            } else {
                logger.info("Couldn't save order " + order);
            }

            return orderToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to save order: " + order);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Order update(Order order) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Order orderToStore = order.toBuilder()
                    .setUpdateTime(Instant.now())
                    .build();

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            UPDATE orders
                            SET update_time = ?,
                            customer_id = ?,
                            scooter_id = ?,
                            start_time = ?,
                            end_time = ?,
                            order_status = ?
                            WHERE id = ?;
                            """);

            statement.setTimestamp(1, Timestamp.from(orderToStore.getUpdateTime()));
            statement.setObject(2, orderToStore.getCustomerId());
            statement.setObject(3, orderToStore.getScooter().getId());
            statement.setTimestamp(4, Timestamp.from(orderToStore.getStartTime()));
            statement.setTimestamp(5, Timestamp.from(orderToStore.getEndTime()));
            statement.setObject(6, orderToStore.getOrderStatus(), Types.OTHER);
            statement.setObject(7, orderToStore.getId(), Types.OTHER);


            boolean isSuccess = statement.executeUpdate() > 0;
            if (isSuccess) {
                logger.info("Order with id :" + orderToStore.getId() + " was successfully updated;");
            } else {
                logger.info("Couldn't update order with id: " + orderToStore.getId());
            }

            return orderToStore;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to update order with id: " + order.getId());
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                            SELECT * FROM orders
                            JOIN scooters ON scooter_id = scooters.id
                            WHERE orders.id = ?"""
            );

            statement.setObject(1, orderId, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                logger.info("Successfully found order with id: " + orderId);

                return Optional.of(Order.createOrderFromResultSet(resultSet));
            }

            logger.info("Couldn't find order with id: " + orderId);

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find order by id: " + orderId, e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }


    @Override
    public Optional<Order> findOngoingOrderByCustomer(UUID customerId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(
                    """
                            SELECT * FROM orders
                            JOIN scooters ON scooter_id = scooters.id
                            WHERE order_status = 'ONGOING'
                            AND customer_id = ?"""
            );

            preparedStatement.setObject(1, customerId, Types.OTHER);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                logger.info("Found ongoing order for customer with id: " + customerId);

                return Optional.of(Order.createOrderFromResultSet(resultSet));
            }

            logger.info("Customer with id: " + customerId + " do not have any ongoing orders.");
            return Optional.empty();

        } catch (SQLException e) {
            logger.info("Error occurred while trying to find ongoing order for customer with id: "
                    + customerId, e);
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(preparedStatement);
        }
    }

    @Override
    public List<Order> findCustomerOrdersByStatus(UUID customerId, OrderStatus orderStatus) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Order> orderList;
        try {
            connection = connectionPool.getConnection();
            orderList = new ArrayList<>();

            statement = connection.prepareStatement(
                    """
                            SELECT * FROM orders
                            JOIN scooters ON scooter_id = scooters.id
                            WHERE customer_id = ?
                            AND order_status = ?;"""
            );
            statement.setObject(1, customerId, Types.OTHER);
            statement.setObject(2, orderStatus, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = Order.createOrderFromResultSet(resultSet);
                orderList.add(order);
            }

            logger.info("Find %d completed orders for customer with id = %s"
                    .formatted(orderList.size(), customerId));

            return orderList;

        } catch (SQLException e) {
            logger.info("Error occurred while trying to find completed orders for customer with id: %s"
                    .formatted(customerId));
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Order> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Order> orderList = new ArrayList<>();
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(
                    """
                             SELECT * FROM orders
                             JOIN scooters ON scooter_id = scooters.id
                            """
            );

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = Order.createOrderFromResultSet(resultSet);
                orderList.add(order);
            }

            logger.info("Find %d orders;".formatted(orderList.size()));

            return orderList;

        } catch (SQLException e) {
            logger.info("Error occurred while trying to find all orders;");
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus orderStatus) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Order> orderList = new ArrayList<>();
        try {
            connection = connectionPool.getConnection();

            statement = connection.prepareStatement("""
                    SELECT * FROM orders
                    JOIN scooters ON scooter_id = scooters.id
                    WHERE order_status = ?""");

            statement.setObject(1, orderStatus, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = Order.createOrderFromResultSet(resultSet);
                orderList.add(order);
            }

            logger.info("Find " + orderList.size() + " " + orderStatus + " orders");

            return orderList;

        } catch (SQLException e) {
            logger.error("Error occurred while trying to find " + orderStatus + " orders;");
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public boolean isExists(Order order) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement("""
                    SELECT * FROM orders
                    WHERE id = ?""");

            statement.setObject(1, order.getId(), Types.OTHER);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            logger.error("Error occurred while trying to check the existing order with id %s"
                    .formatted(order.getId()));
            throw new RuntimeException(e);
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
            statement = connection.prepareStatement(
                    """
                            DELETE FROM orders
                            WHERE id = ?"""
            );

            statement.setObject(1, id, Types.OTHER);

            boolean success = statement.executeUpdate() > 0;

            logger.info((success)
                    ? "Successfully delete order with id: %s".formatted(id)
                    : "Couldn't find order with id: %s".formatted(id));


            return success;

        } catch (SQLException e) {
            logger.error("Error occurred while trying delete order with id: %s".formatted(id));
            throw new ElementNotFoundException(e.getMessage(), e);
        } finally {
            connectionPool.releaseConnection(connection);
            closeStatement(statement);
        }
    }
}
