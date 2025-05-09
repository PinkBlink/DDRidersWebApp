import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.filter.ExceptionHandlerFilter;

import java.io.IOException;

public class ExceptionHandlerFilterTest {
    private final ExceptionHandlerFilter handlerFilter = new ExceptionHandlerFilter();

    @Test
    public void handlerRespondsWith404() throws IOException, ServletException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_NOT_FOUND;
        final var expectedMessage = "Customer not found";

        Mockito.doThrow(new NotFoundException(expectedMessage))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus, expectedMessage);
    }

    @Test
    public void handlerRespondsWith500() throws IOException, ServletException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        Mockito.doThrow(new DatabaseException("Couldn't connect to db."))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus);
    }

    @Test
    public void handlerRespondsWith401() throws IOException, ServletException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_UNAUTHORIZED;
        final var message = "Wrong email or password";

        Mockito.doThrow(new UnauthorizedException(message))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus);
    }

    @Test
    public void handlerRespondsWith409IfDuplicate() throws IOException, ServletException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_CONFLICT;
        final var expectedMessage = "Customer is already exists";

        Mockito.doThrow(new DuplicateEntryException(expectedMessage))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus, expectedMessage);
    }

    @Test
    public void handlerRespondsWith409IfIllegalStatus() throws IOException, ServletException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_CONFLICT;
        final var expectedMessage = "Scooter is already rented";

        Mockito.doThrow(new IllegalStatusException(expectedMessage))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus, expectedMessage);
    }

    @Test
    public void handlerRespondsWith400() throws ServletException, IOException {
        //given
        final var request = Mockito.mock(HttpServletRequest.class);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var filterChain = Mockito.mock(FilterChain.class);

        final var expectedResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        final var expectedMessage = "Email is null";

        Mockito.doThrow(new BadRequestException(expectedMessage))
            .when(filterChain)
            .doFilter(request, response);

        //when
        handlerFilter.doFilter(request, response, filterChain);

        //then
        Mockito.verify(response).sendError(expectedResponseStatus, expectedMessage);
    }
}
