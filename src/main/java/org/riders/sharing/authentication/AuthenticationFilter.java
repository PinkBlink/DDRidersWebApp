package org.riders.sharing.authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.ApplicationConfig;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.riders.sharing.utils.ErrorMessages.CUSTOMER_NOT_FOUND;
import static org.riders.sharing.utils.ErrorMessages.EXPIRED_TOKEN;
import static org.riders.sharing.utils.ErrorMessages.INVALID_TOKEN;
import static org.riders.sharing.utils.ErrorMessages.TOKEN_IS_EMPTY;
import static org.riders.sharing.utils.ErrorMessages.UNAUTHORIZED_ACCESS;

@WebFilter("/main-servlet/secure/*")
public class AuthenticationFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);

    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ApplicationConfig config = ApplicationConfig.getInstance();

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;

        try {
            final var tokenDecoder = new AuthTokenDecoder(config.getAlgorithm());

            final var stringToken = tokenDecoder.getAccessTokenFromRequest(request);
            final var decodedToken = tokenDecoder.decode(stringToken);

            final var idFromToken = tokenDecoder.getIdFromToken(decodedToken);
            final var emailFromToken = tokenDecoder.getEmailFromAccessToken(decodedToken);

            final var customerFromDb = customerService.getById(idFromToken);

            if (!customerFromDb.getEmail().equals(emailFromToken)) {
                throw new UnauthorizedException("Emails do not match.");
            }

            LOGGER.info("Successfully authenticated customer with id {}", customerFromDb.getId());
            filterChain.doFilter(request, response);
        } catch (BadRequestException e) {
            LOGGER.error("Authorization header is missing, or the token is empty.", e);
            response.sendError(SC_BAD_REQUEST, TOKEN_IS_EMPTY);
        } catch (TokenExpiredException e) {
            LOGGER.error("Access token is expired", e);
            response.sendError(SC_UNAUTHORIZED, EXPIRED_TOKEN);
        } catch (JWTVerificationException | InvalidTokenException e) {
            LOGGER.error("Invalid token!", e);
            response.sendError(SC_UNAUTHORIZED, INVALID_TOKEN);
        } catch (NoElementException e) {
            LOGGER.error("Couldn't find Customer.", e);
            response.sendError(SC_NOT_FOUND, CUSTOMER_NOT_FOUND);
        } catch (UnauthorizedException e) {
            LOGGER.error("Unauthorized access attempt", e);
            response.sendError(SC_UNAUTHORIZED, UNAUTHORIZED_ACCESS);
        }
    }
}
