package org.riders.sharing.utils;

public class ErrorMessages {
    public static final String SCOOTER_IS_RENTED = """
        {
        "error" : "Scooter is already rented"
        }
        """;

    public static final String SCOOTER_OR_CUSTOMER_NOT_FOUND = """
        {
        "error" : "Scooter or Customer not found"
        }
        """;
    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String EXPIRED_TOKEN = "Token is expired";
    public static final String TOKEN_IS_EMPTY = "Token is empty";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String INVALID_TOKEN = "Invalid token!";
}
