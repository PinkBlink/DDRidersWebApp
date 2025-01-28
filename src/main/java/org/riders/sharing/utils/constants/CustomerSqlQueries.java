package org.riders.sharing.utils.constants;

public class CustomerSqlQueries {
    public static final String INSERT_QUERY = "INSERT INTO customers(customer_id,name,surname,email,password_hash) " +
            "VALUES(?, ?, ?, ?, ?);";
    public static final String UPDATE_QUERY = "UPDATE customers " +
            "SET name = ?" +
            ", SET surname = ?," +
            ", SET email = ?" +
            ", SET password_hash = ?" +
            ", SET scooter_rent_id = ?" +
            "WHERE customer_id = ?;";
}
