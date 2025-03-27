package org.riders.sharing.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.util.UUID;

public class SecurityUtils {
    private static final Logger logger = LogManager.getLogger(SecurityUtils.class);

    public static void hasCustomerAccessOrThrow(HttpServletRequest request) {
        try {

            CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());
            String token = TokenUtils.getAuthorizationToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(token);
            UUID customerId = UUID.fromString(decodedJWT.getSubject());
            customerService.getById(customerId);
            TokenUtils.isTokenExpiredThrow(decodedJWT.getExpiresAtAsInstant());

        } catch (RuntimeException e) {
            logger.error("Access error:" + e.getMessage());
            throw new NoAccessException("Access error: " + e.getMessage());
        }
    }
}
