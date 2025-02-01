package org.riders.sharing.factory.impl;

import org.riders.sharing.factory.OrderFactory;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.utils.constants.OrderSQLColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderFactoryImpl implements OrderFactory {
    @Override
    public Order createOrder(int orderId, int scooterId, int customerId, LocalDateTime startTime) {
        return new Order(orderId, customerId, scooterId, startTime);
    }

    @Override
    public Order createOrder(int orderId, int customerId, int scooterId, LocalDateTime startTime,
                             LocalDateTime endTime, OrderStatus status) {
        return new Order(orderId, customerId, scooterId, startTime, endTime, status);
    }


    @Override
    public Order createOrderFromResultSet(ResultSet resultSet) throws SQLException {
        int orderId = resultSet.getInt(OrderSQLColumns.ORDER_ID.getName());
        int customerId = resultSet.getInt(OrderSQLColumns.CUSTOMER_ID.getName());
        int scooterId = resultSet.getInt(OrderSQLColumns.SCOOTER_ID.getName());

        LocalDateTime startTime = resultSet.getTimestamp(
                        OrderSQLColumns.START_TIME.getName())
                .toLocalDateTime();

        LocalDateTime endTime = resultSet.getTimestamp(
                        OrderSQLColumns.END_TIME.getName())
                .toLocalDateTime();

        OrderStatus orderStatus = OrderStatus.valueOf(resultSet.getString(
                OrderSQLColumns.ORDER_STATUS.getName()));
        return new Order(orderId, customerId, scooterId, startTime, endTime, orderStatus);
    }
}