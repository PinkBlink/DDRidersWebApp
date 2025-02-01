package org.riders.sharing.utils.constants;

public enum OrderSQLColumns {
    TABLE_NAME("orders"),
    ORDER_ID("order_id"),
    CUSTOMER_ID("customer_id"),
    SCOOTER_ID("scooter_id"),
    START_TIME("start_time"),
    END_TIME("end_time"),
    ORDER_STATUS("order_status");

    private final String name;

    OrderSQLColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
