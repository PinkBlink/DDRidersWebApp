package org.riders.sharing.factory;

import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public interface OrderFactory {
    Order createOrder(int id, int customerId, Scooter scooter, LocalDateTime period);

    Order createOrder(int id, int customerId, Scooter scooter, LocalDateTime startTime
            , LocalDateTime endTime, OrderStatus status);

    Order createOrderFromResultSet(ResultSet resultSet) throws SQLException;
}
