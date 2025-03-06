package org.riders.sharing.command;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CustomerDTO;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.TokenUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class GetCustomerCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            String token = TokenUtils.getAuthorizationToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(token);
            UUID id = UUID.fromString(decodedJWT.getSubject());

            Customer customer = customerService.getById(id);
            CustomerDTO customerDTO = CustomerDTO.parse(customer);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.println(objectMapper.writeValueAsString(customerDTO));
            }

        } catch (NoAccessException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
