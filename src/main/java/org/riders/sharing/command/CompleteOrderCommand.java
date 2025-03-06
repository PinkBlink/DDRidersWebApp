package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.service.impl.ScooterServiceImpl;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

public class CompleteOrderCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderService orderService = new OrderServiceImpl(new OrderRepositoryImpl());
        ScooterService scooterService = new ScooterServiceImpl(new ScooterRepositoryImpl());

        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            String requestBody = ServletUtils.getRequestBody(request);
            OrderDTO orderDTO = objectMapper.readValue(requestBody, OrderDTO.class);

            Order order = orderService.getById(orderDTO.getOrderId());

            Order completedOrder = order.complete();

            orderService.updateOrder(completedOrder);
            scooterService.update(completedOrder.getScooter());

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NoAccessException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);

        } catch (ElementNotFoundException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_NOT_FOUND, e);
        }
    }
}
