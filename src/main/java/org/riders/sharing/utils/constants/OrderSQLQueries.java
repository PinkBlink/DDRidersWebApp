package org.riders.sharing.utils.constants;

public class OrderSQLQueries {
    public static final String INSERT_ORDER =
            "INSERT INTO orders(order_id, customer_id, scooter_id, start_time, end_time, order_status) " +
                    "VALUES(?, ?, ?, ?, ?, ?);";
    public static final String UPDATE_ORDER =
            "UPDATE orders " +
                    "SET customer_id = ?, " +
                    "scooter_id = ?, " +
                    "start_time = ?, " +
                    "end_time = ?, " +
                    "order_status = ? " +
                    "WHERE order_id = ?; ";
    public static final String FIND_ALL = "SELECT order_id, customer_id, orders.scooter_id, start_time, end_time, order_status, " +
            "scooters.scooter_type, scooters.scooter_status, scooters.battery_level " +
            "FROM orders " +
            "JOIN scooters ON orders.scooter_id = scooters.scooter_id ";
    public static final String FIND_ORDER_BY_ID =
            FIND_ALL +
                    "WHERE order_id = ? ";
    public static final String FIND_ONGOING_ORDERS =
            FIND_ALL +
                    "WHERE order_status = 'ONGOING' ";
    public static final String FIND_COMPLETED_ORDERS =
            FIND_ALL +
                    "WHERE order_status = 'COMPLETED' ";
    public static final String FIND_ONGOING_ORDER_BY_CUSTOMER_ID=
            FIND_ONGOING_ORDERS +
                    "AND customer_id = ? ";
    public static final String DELETE_ORDER =
            "DELETE FROM orders " +
                    "WHERE order_id = ?";
}