package org.riders.sharing.utils.constants;

public class CustomerSqlQueries {
    public static final String INSERT_QUERY =
            "INSERT INTO customers(customer_id,name,surname,email,password_hash) " +
                    "VALUES(?, ?, ?, ?, ?);";
    public static final String FIND_USER_BY_ID =
            "SELECT * " +
                    "FROM customers " +
                    "WHERE id = ?;";

    public static final String FIND_ALL_CUSTOMERS =
            "SELECT * FROM customers;";

    public static final String DELETE_CUSTOMER_BY_ID =
            "DELETE FROM customers" +
                    "WHERE customer_id = ?;";
    public static final String UPDATE_CUSTOMER_FIELDS =
            "UPDATE customers " +
                    "SET name = ?" +
                    "SET surname = ?" +
                    "SET email = ?" +
                    "SET password_hash = ?" +
                    "WHERE customer_id = ?;";
}
