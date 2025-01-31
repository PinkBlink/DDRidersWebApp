package org.riders.sharing.utils.constants;

public enum CustomerSqlColumns {
    CUSTOMER_ID(1, "customer_id"),
    NAME(2, "name"),
    SURNAME(3, "surname"),

    EMAIL(4, "email"),

    PASSWORD_HASH(5, "password_hash");
    private final int columnNumber;
    private final String columnName;

    CustomerSqlColumns(int columnNumber, String columnName) {
        this.columnNumber = columnNumber;
        this.columnName = columnName;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }
}
