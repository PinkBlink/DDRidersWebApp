package org.riders.sharing.command;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

public class LogoutCommand extends Command {
    private final Logger logger = LogManager.getLogger(LogoutCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            Cookie cookie = new Cookie("refreshToken", "");

            cookie.setPath("/refresh");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(0);

            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NoAccessException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
