package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.utils.ModelMapper;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class CompleteOrderCommand extends Command {
    private final Logger logger = LogManager.getLogger(CompleteOrderCommand.class);
    private final OrderService orderService;

    public CompleteOrderCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = getRequestBody(request);
            final var orderDto = ModelMapper.parse(requestBody, OrderDto.class);

            final var completedOrder = orderService.completeOrder(orderDto);

            final var completedOrderDto = OrderDto.fromOrder(completedOrder);
            final var completedOrderDtoJson = ModelMapper.toJsonString(completedOrderDto);

            response.setStatus(SC_OK);
            response.setContentType(JSON_CONTENT_TYPE);

            writeResponse(response, completedOrderDtoJson);
        } catch (BadRequestException e) {
            logger.error("Failed to complete order due to bad request: {}", e.getMessage());
            response.setStatus(SC_BAD_REQUEST);
        } catch (IllegalStatusException e) {
            logger.error("Illegal Status for order or scooter", e);
            response.setStatus(SC_CONFLICT);
        } catch (NoElementException e) {
            logger.error("Failed to complete order. Scooter or Order doesn't exist.", e);
            response.setStatus(SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to complete order with message: {}", e.getMessage());
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}
