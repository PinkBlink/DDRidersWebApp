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
                    "end_time = ?, "  +
                    "status = ? " +
                    "WHERE order_id = ?; ";
    public static final String FIND_ORDER_BY_ID =
            "SELECT * FROM orders " +
                    "WHERE order_id = ?;";
    public static final String FIND_ONGOING_ORDERS =
            "SELECT * FROM orders " +
                    "WHERE status = ONGOING;";
    public static final String FIND_COMPLETED_ORDERS =
            "SELECT * FROM orders " +
                    "WHERE status = COMPLETED;";
    public static final String FIND_ALL =
            "SELECT * FROM orders;";
    public static final String DELETE_ORDER =
            "DELETE FROM orders " +
                    "WHERE order_id = ?";
}
