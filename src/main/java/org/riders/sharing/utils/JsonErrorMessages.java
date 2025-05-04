package org.riders.sharing.utils;

public interface JsonErrorMessages {
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
}
