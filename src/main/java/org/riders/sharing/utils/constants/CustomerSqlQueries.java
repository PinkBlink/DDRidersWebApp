package org.riders.sharing.utils.constants;

public class CustomerSqlQueries {
    public static final String INSERT_QUERY =
            "INSERT INTO customers(customer_id,name,surname,email,password_hash) " +
                    "VALUES(?, ?, ?, ?, ?);";
    public static final String UPDATE_QUERY =
            "UPDATE customers " +
                    "SET name = ?" +
                    ", SET surname = ?," +
                    ", SET email = ?" +
                    ", SET password_hash = ?" +
                    ", SET scooter_rent_id = ?" +
                    "WHERE customer_id = ?;";
    public static final String FIND_USER_BY_ID =
            "SELECT customer_id, name, surname, email, password_hash " +
                    "FROM customers " +
                    "WHERE customer_id = ?;";

    public static final String FIND_CUSTOMER_BY_EMAIL =
            "SELECT(customer_id, name, surname, email, password_hash) " +
                    "FROM customers " +
                    "WHERE customer_id=?;";
}
