package org.riders.sharing.utils;

public class ErrorMessages {
    public static final String CUSTOMER_NOT_FOUND = "Customer %s not found";
    public static final String SCOOTER_NOT_FOUND = "Scooter %s not found";
    public static final String ORDER_NOT_FOUND = "Order %s not found";

    public static final String CUSTOMER_DUPLICATE = "Customer %s already exists";
    public static final String SCOOTER_DUPLICATE = "Scooter %s is already exists";
    public static final String ORDER_DUPLICATE = "Order %s is already exists";

    public static final String EXPIRED_TOKEN = "Token is expired";
    public static final String TOKEN_IS_EMPTY = "Token is empty";
    public static final String INVALID_TOKEN = "Invalid token!";
    public static final String CLAIM_IS_MISSING = "Claim %s is missing";
    public static final String HEADER_IS_MISSING = "Header %s is missing";

    public static final String ERROR_LOADING_CONFIG = "Error loading config.yml";
    public static final String CANT_DEREGISTER_DRIVER = "Can't deregister driver";
    public static final String NO_CONNECTIONS_TO_DB = "No connections to database";
    public static final String CANT_CREATE_CONNECTION = "Can't create connection to database ";
    public static final String CANT_CLOSE_CONNECTION = "Can't close connection";

    public static final String WRONG_EMAIL_PASSWORD = "Wrong email or password";
    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String NULL_EMAIL_OR_PASSWORD = "Email or password is null";
    public static final String NULL_ID_OR_PASSWORD = "Id or password is null";
    public static final String NULL_SCOOTER_OR_CUSTOMER = "Scooter or customer id is null.";
    public static final String NULL_CUSTOMER_ID = "Customer id is null";
    public static final String NULL_ORDER_ID = "Order id is null";

    public static final String ORDER_ALREADY_COMPLETED = "Order with id %s is already completed.";
    public static final String SCOOTER_ALREADY_RENTED = "Scooter with id %s is already rented";
    public static final String SCOOTER_ALREADY_AVAILABLE = "Scooter with id %s is already available";

    public static final String ERR_MAP_JSON_TO_OBJECT = "Couldn't parse JSON to Object";
    public static final String ERR_MAP_OBJECT_TO_JSON = "Couldn't parse Object to JSON";

    public static final String PAGE_SIZE_IS_ZERO = "PageSize is zero";
    public static final String REQUEST_IS_EMPTY = "Request body is empty.";


}
