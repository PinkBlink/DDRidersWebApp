package org.riders.sharing.command;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.OrderDTO;
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
import java.util.List;
import java.util.UUID;

public class GetCompletedCustomerOrdersCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderService orderService = new OrderServiceImpl(new OrderRepositoryImpl());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            String token = TokenUtils.getAuthorizationToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(token);
            UUID customerId = UUID.fromString(decodedJWT.getSubject());

            List<Order> orderList = orderService.getAllCustomerCompletedOrders(customerId);
            List<OrderDTO> orderDTOList = orderList.stream()
                    .map(OrderDTO::parse).toList();

            if (orderList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                try (PrintWriter writer = response.getWriter()) {
                    writer.println(objectMapper.writeValueAsString(orderDTOList));
                }
            }
        } catch (NoAccessException e) {

            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
