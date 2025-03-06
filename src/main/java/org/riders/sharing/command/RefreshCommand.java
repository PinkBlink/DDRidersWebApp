package org.riders.sharing.command;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.exception.ExpiredTokenException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.TokenUtils;

import java.io.IOException;
import java.util.UUID;

public class RefreshCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());

            String oldRefreshToken = TokenUtils.getRefreshToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(oldRefreshToken);

            TokenUtils.isTokenExpiredThrow(decodedJWT.getExpiresAtAsInstant());

            UUID customerId = UUID.fromString(decodedJWT.getSubject());
            Customer customer = customerService.getById(customerId);

            String newAccessToken = TokenUtils.getNewAccessToken(customer);
            String newRefreshToken = TokenUtils.getNewRefreshToken(customer);

            Cookie cookie = new Cookie("refreshToken", newRefreshToken);
            cookie.setPath("/refresh");
            cookie.setSecure(true);
            cookie.setMaxAge(600);
            cookie.setHttpOnly(true);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setHeader("Authorization", "Bearer " + newAccessToken);
            response.addCookie(cookie);

        } catch (ElementNotFoundException | ExpiredTokenException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}