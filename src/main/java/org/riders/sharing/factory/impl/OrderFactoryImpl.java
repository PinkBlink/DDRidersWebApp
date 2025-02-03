package org.riders.sharing.factory.impl;

import org.riders.sharing.factory.OrderFactory;
import org.riders.sharing.factory.ScooterFactory;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.utils.constants.OrderSQLColumns;
import org.riders.sharing.utils.constants.ScooterSQLColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OrderFactoryImpl implements OrderFactory {
    @Override
    public Order createOrder(int orderId, int customerId, Scooter scooter, LocalDateTime startTime) {
        return new Order(orderId, customerId, scooter, startTime);
    }

    @Override
    public Order createOrder(int orderId, int customerId, Scooter scooter, LocalDateTime startTime,
                             LocalDateTime endTime, OrderStatus status) {
        return new Order(orderId, customerId, scooter, startTime, endTime, status);
    }

    @Override
    public Order createOrderFromResultSet(ResultSet resultSet) throws SQLException {
        ScooterFactory scooterFactory = new ScooterFactoryImpl();
        int orderId = resultSet.getInt(OrderSQLColumns.ORDER_ID.getName());
        int customerId = resultSet.getInt(OrderSQLColumns.CUSTOMER_ID.getName());
        int scooterId = resultSet.getInt(OrderSQLColumns.SCOOTER_ID.getName());
        ScooterType scooterType = ScooterType.valueOf(
                resultSet.getString(ScooterSQLColumns.SCOOTER_TYPE.getName()));
        ScooterStatus scooterStatus = ScooterStatus.valueOf(
                resultSet.getString(ScooterSQLColumns.SCOOTER_STATUS.getName())
        );
        int batteryLevel = resultSet.getInt(ScooterSQLColumns.BATTERY_LEVEL.getName());
        LocalDateTime startTime = resultSet.getTimestamp(
                        OrderSQLColumns.START_TIME.getName())
                .toLocalDateTime();

        Timestamp endTimeTimestamp = resultSet.getTimestamp(OrderSQLColumns.END_TIME.getName());
        LocalDateTime endTime = endTimeTimestamp == null
                ? null
                : endTimeTimestamp.toLocalDateTime();

        OrderStatus orderStatus = OrderStatus.valueOf(resultSet.getString(
                OrderSQLColumns.ORDER_STATUS.getName()));
        Scooter scooter = scooterFactory.createScooter(scooterId, scooterType, scooterStatus, batteryLevel);
        return new Order(orderId, customerId, scooter, startTime, endTime, orderStatus);
    }
}