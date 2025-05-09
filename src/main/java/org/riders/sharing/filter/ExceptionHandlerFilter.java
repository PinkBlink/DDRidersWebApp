package org.riders.sharing.filter;

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
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.UnauthorizedException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(ExceptionHandlerFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException {
        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;

        try {
            chain.doFilter(request, response);
        } catch (IllegalStatusException e) {
            logger.error("Illegal status of entity {}", e.getMessage(), e);
            response.sendError(SC_CONFLICT, e.getMessage());
        } catch (DuplicateEntryException e) {
            logger.error("Resource is already exists {}", e.getMessage(), e);
            response.sendError(SC_CONFLICT, e.getMessage());
        } catch (NotFoundException e) {
            logger.error("Failed to find resource: {}", e.getMessage(), e);
            response.sendError(SC_NOT_FOUND, e.getMessage());
        } catch (InvalidTokenException | UnauthorizedException e) {
            logger.error("Unauthorized: {}", e.getMessage(), e);
            response.sendError(SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            logger.error("Failed due to bad request: {}", e.getMessage(), e);
            response.sendError(SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed with message: {}", e.getMessage(), e);
            response.sendError(SC_INTERNAL_SERVER_ERROR);
        }
    }
}
