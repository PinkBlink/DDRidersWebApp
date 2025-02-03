package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPull;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.OrderFactory;
import org.riders.sharing.factory.impl.OrderFactoryImpl;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.utils.constants.OrderSQLQueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OrderRepositoryImpl implements OrderRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final OrderFactory orderFactory = new OrderFactoryImpl();
    private final ConnectionPull connectionPull = ConnectionPull.getInstance();

    @Override
    public void saveOrder(Order order) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(OrderSQLQueries.INSERT_ORDER);

            int orderId = order.getOrderId();
            int customerId = order.getCustomerId();
            int scooterId = order.getScooter().getId();
            Timestamp startTime = Timestamp.valueOf(order.getStartTime());
            Timestamp endTime = order.getEndTime() == null
                    ? null
                    : Timestamp.valueOf(order.getEndTime());
            OrderStatus orderStatus = order.getOrderStatus();

            statement.setInt(1, orderId);
            statement.setInt(2, customerId);
            statement.setInt(3, scooterId);
            statement.setTimestamp(4, startTime);
            statement.setTimestamp(5, endTime);
            statement.setObject(6, orderStatus, Types.OTHER);

            boolean isSuccess = statement.executeUpdate() > 0;
            if (isSuccess) {
                logger.info("Order saved successfully; " + order);
            } else {
                logger.info("Couldn't save order " + order);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to save order: " + order);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void updateOrder(Order order) {
        int orderId = order.getOrderId();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(OrderSQLQueries.UPDATE_ORDER);

            fillOrderStatement(statement, order, true);//need tests

            boolean isSuccess = statement.executeUpdate() > 0;
            if (isSuccess) {
                logger.info("Order with id :" + orderId + " was successfully updated;");
            } else {
                logger.info("Couldn't update order with id: " + orderId);
            }
        } catch (SQLException e) {
            logger.error("Error occurred while trying to update order with id: " + order.getOrderId());
            throw new RuntimeException(e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public Optional<Order> findOrderById(int orderId) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(OrderSQLQueries.FIND_ORDER_BY_ID);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                logger.info("Successfully found order with id: " + orderId);
                return Optional.of(orderFactory.createOrderFromResultSet(resultSet));
            }
            logger.info("Couldn't find order with id: " + orderId);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error occurred while trying to find order by id: " + orderId, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }


    @Override
    public Optional<Order> findOngoingOrderByCustomer(Customer customer) throws RepositoryException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int customerId = customer.getCustomerId();
        try {
            connection = connectionPull.getConnection();
            preparedStatement = connection.prepareStatement(OrderSQLQueries.FIND_ONGOING_ORDER_BY_CUSTOMER_ID);
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info("Found ongoing order for customer with id: " + customerId);
                return Optional.of(orderFactory.createOrderFromResultSet(resultSet));
            }
            logger.info("Customer with id: " + customerId + " do not have any ongoing orders.");
            return Optional.empty();
        } catch (SQLException e) {
            logger.info("Error occurred while trying to find ongoing order for customer with id: "
                    + customerId, e);
            throw new RepositoryException(e.getMessage(), e);
        }finally {
            connectionPull.releaseConnection(connection);
            closeStatement(preparedStatement);
        }
    }

    @Override
    public List<Order> findAll() throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Order> orderList = new ArrayList<>();
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(OrderSQLQueries.FIND_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = orderFactory.createOrderFromResultSet(resultSet);
                orderList.add(order);
            }
            logger.info("Find " + orderList.size() + " orders;");
            return orderList;
        } catch (SQLException e) {
            logger.info("Error occurred while trying to find all orders;");
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus orderStatus) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        List<Order> orderList = new ArrayList<>();
        try {
            connection = connectionPull.getConnection();

            statement = (orderStatus == OrderStatus.ONGOING)
                    ? connection.prepareStatement(OrderSQLQueries.FIND_ONGOING_ORDERS)
                    : connection.prepareStatement(OrderSQLQueries.FIND_COMPLETED_ORDERS);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = orderFactory.createOrderFromResultSet(resultSet);
                orderList.add(order);
            }
            logger.info("Find " + orderList.size() + " " + orderStatus + " orders");
            return orderList;
        } catch (SQLException e) {
            logger.error("Error occurred while trying to find " + orderStatus + " orders;");
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void deleteOrder(Order order) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        int orderId = order.getOrderId();
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(OrderSQLQueries.DELETE_ORDER);
            statement.setInt(1, order.getOrderId());

            int result = statement.executeUpdate();

            logger.info((result > 0)
                    ? "Successfully delete order with id: " + orderId
                    : "Couldn't find order with id: " + orderId);
        } catch (SQLException e) {
            logger.error("Error occurred while trying delete order with id: " + orderId);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    private void fillOrderStatement(PreparedStatement statement, Order order, boolean isUpdate)
            throws SQLException {
        int index = isUpdate ? 0 : 1;
        statement.setInt(1 + index, order.getCustomerId());
        statement.setInt(2 + index, order.getScooter().getId());
        statement.setTimestamp(3 + index, Timestamp.valueOf(order.getStartTime()));
        statement.setTimestamp(4 + index, Timestamp.valueOf(order.getEndTime()));
        statement.setObject(5 + index, order.getOrderStatus(), Types.OTHER);
        statement.setInt(isUpdate
                        ? 6
                        : 1
                , order.getOrderId());
    }
}