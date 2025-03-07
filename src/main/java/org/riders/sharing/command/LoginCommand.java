package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.LoginDTO;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.WrongEmailOrPasswordException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.TokenUtils;

import java.io.IOException;

public class LoginCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String body = ServletUtils.getRequestBody(request);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            LoginDTO loginDTO = objectMapper.readValue(body, LoginDTO.class);

            CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());

            Customer customer = customerService.logIn(
                    loginDTO.getEmail()
                    , PasswordEncryptor.hashPassword(loginDTO.getPassword()));

            String accessToken = TokenUtils.getNewAccessToken(customer);
            String refreshToken = TokenUtils.getNewRefreshToken(customer);

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/refresh");
            cookie.setMaxAge(600);

            response.setStatus(200);
            response.setContentType("application/json");
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.addCookie(cookie);

        } catch (BadRequestException | WrongEmailOrPasswordException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
