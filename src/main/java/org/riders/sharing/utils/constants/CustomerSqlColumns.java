package org.riders.sharing.utils.constants;

public enum CustomerSqlColumns {
    TABLE_NAME("customers"),
    CUSTOMER_ID("customer_id"),
    NAME("name"),
    SURNAME("surname"),

    EMAIL("email"),

    PASSWORD_HASH("password_hash");
    private final String name;

    CustomerSqlColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
