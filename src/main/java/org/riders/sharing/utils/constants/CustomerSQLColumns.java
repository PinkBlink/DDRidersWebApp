package org.riders.sharing.utils.constants;

public enum CustomerSQLColumns {
    TABLE_NAME("customers"),
    CUSTOMER_ID("customer_id"),
    NAME("name"),
    SURNAME("surname"),

    EMAIL("email"),

    PASSWORD_HASH("password_hash");
    private final String name;

    CustomerSQLColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
