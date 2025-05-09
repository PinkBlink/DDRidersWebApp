import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.authentication.AuthTokenGenerator;
import org.riders.sharing.authentication.AuthenticationFilter;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.config.ApplicationConfig;

import java.io.IOException;
import java.time.Instant;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.riders.sharing.authentication.AuthConstants.AUTH_HEADER;
import static org.riders.sharing.authentication.AuthConstants.BEARER;
import static org.riders.sharing.authentication.AuthConstants.EMAIL_CLAIM;
import static org.riders.sharing.authentication.AuthConstants.EMPTY_STRING;
import static org.riders.sharing.utils.ErrorMessages.CUSTOMER_NOT_FOUND;
import static org.riders.sharing.utils.ErrorMessages.EXPIRED_TOKEN;
import static org.riders.sharing.utils.ErrorMessages.INVALID_TOKEN;
import static org.riders.sharing.utils.ErrorMessages.TOKEN_IS_EMPTY;
import static org.riders.sharing.utils.ErrorMessages.UNAUTHORIZED_ACCESS;

public class AuthenticationFilterTest extends BaseTest implements CustomerTestData {
    private final ApplicationConfig appConfig = ApplicationConfig.getInstance();
    private final AuthTokenDecoder tokenDecoder = new AuthTokenDecoder(appConfig.getAlgorithm());
    private final AuthTokenGenerator tokenGenerator = new AuthTokenGenerator(
        appConfig.getAccessTokenTtl(),
        appConfig.getRefreshTokenTtl(),
        appConfig.getAlgorithm()
    );
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);

    private final AuthenticationFilter authenticationFilter = new AuthenticationFilter(customerService, tokenDecoder);

    @Test
    public void doFilterAllowsAccess() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var token = tokenGenerator.generateNewAccessToken(savedCustomer);

        when(request.getHeader(AUTH_HEADER)).thenReturn(BEARER + token);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterDeclineAccessWith400IfNoHeader() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var expectedResponseStatus = SC_BAD_REQUEST;
        final var expectedResponseMessage = TOKEN_IS_EMPTY;

        when(request.getHeader(AUTH_HEADER)).thenReturn(null);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }

    @Test
    public void doFilterDeclineAccessWith400IfEmptyToken() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var expectedResponseStatus = SC_BAD_REQUEST;
        final var expectedResponseMessage = TOKEN_IS_EMPTY;

        when(request.getHeader(AUTH_HEADER)).thenReturn(EMPTY_STRING);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }

    @Test
    public void doFilterDeclineAccessWith404IfCustomerNotFound() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var unsavedCustomer = aCustomer().build();
        final var token = tokenGenerator.generateNewAccessToken(unsavedCustomer);

        final var expectedResponseStatus = SC_NOT_FOUND;
        final var expectedResponseMessage = CUSTOMER_NOT_FOUND;

        when(request.getHeader(AUTH_HEADER)).thenReturn(BEARER + token);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }

    @Test
    public void doFilterDeclineAccessWith401IfExpiredToken() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var customer = customerRepository.save(aCustomer().build());
        final var expiredToken = JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim(EMAIL_CLAIM, customer.getEmail())
            .withExpiresAt(Instant.now())
            .sign(appConfig.getAlgorithm());

        final var expectedResponseStatus = SC_UNAUTHORIZED;
        final var expectedResponseMessage = EXPIRED_TOKEN;

        when(request.getHeader(AUTH_HEADER)).thenReturn(expiredToken);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }

    @Test
    public void doFilterDeclineAccessWith401IfWrongEmail() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var customer = customerRepository.save(aCustomer().build());
        final var wrongEmailForToken = "wrong";

        final var token = JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim(EMAIL_CLAIM, wrongEmailForToken)
            .sign(appConfig.getAlgorithm());

        final var expectedResponseStatus = SC_UNAUTHORIZED;
        final var expectedResponseMessage = UNAUTHORIZED_ACCESS;

        when(request.getHeader(AUTH_HEADER)).thenReturn(token);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }

    @Test
    public void doFilterDeclineAccessWith401IfWrongSignature() throws ServletException, IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var filterChain = mock(FilterChain.class);

        final var customer = aCustomer().build();
        final var wrongSignature = "clean_pussy_walkers";

        final var token = JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim(EMAIL_CLAIM, customer.getEmail())
            .sign(Algorithm.HMAC256(wrongSignature));


        final var expectedResponseStatus = SC_UNAUTHORIZED;
        final var expectedResponseMessage = INVALID_TOKEN;

        when(request.getHeader(AUTH_HEADER)).thenReturn(token);

        //when
        authenticationFilter.doFilter(request, response, filterChain);

        //then
        verify(response).sendError(expectedResponseStatus, expectedResponseMessage);
    }
}
