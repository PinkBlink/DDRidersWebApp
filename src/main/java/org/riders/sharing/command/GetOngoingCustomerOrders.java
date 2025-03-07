package org.riders.sharing.command;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.OrderDTO;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.TokenUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class GetOngoingCustomerOrders extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OrderService orderService = new OrderServiceImpl(new OrderRepositoryImpl());

        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            String accessTokenString = TokenUtils.getAuthorizationToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(accessTokenString);
            UUID customerId = UUID.fromString(decodedJWT.getSubject());

            Order activeOrder = orderService.getOngoingOrder(customerId);
            OrderDTO orderDTO = OrderDTO.parse(activeOrder);

            response.setStatus(200);
            response.setContentType("application/json");

            try (PrintWriter writer = response.getWriter()) {
                writer.println(objectMapper.writeValueAsString(orderDTO));
            }
        } catch (NoAccessException e) {
            logger.error(e);
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (ElementNotFoundException e) {
            logger.info(e);
            ServletUtils.handleException(response, HttpServletResponse.SC_NO_CONTENT, e);
        }
    }
}
