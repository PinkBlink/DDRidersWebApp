package org.riders.sharing.utils;

public interface ErrorMessages {
    String SCOOTER_IS_RENTED = """
        {
        "error" : "Scooter is already rented"
        }
        """;

    String SCOOTER_OR_CUSTOMER_NOT_FOUND = """
        {
        "error" : "Scooter or Customer not found"
        }
        """;
    String CUSTOMER_NOT_FOUND = "Customer not found";
    String EXPIRED_TOKEN = "Token is expired";
    String TOKEN_IS_EMPTY = "Token is empty";
    String UNAUTHORIZED_ACCESS = "Unauthorized access";
    String INVALID_TOKEN = "Invalid token!";
}
