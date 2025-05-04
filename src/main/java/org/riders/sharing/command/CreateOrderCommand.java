package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.utils.JsonErrorMessages;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.utils.ServletUtils;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class CreateOrderCommand extends Command {
    private final Logger logger = LogManager.getLogger(CreateOrderCommand.class);
    private final OrderService orderService;

    public CreateOrderCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var createOrderDto = ModelMapper.parse(requestBody, CreateOrderDto.class);

            final var order = orderService.createOrder(createOrderDto);
            final var orderDto = OrderDto.fromOrder(order);
            final var orderDtoJson = ModelMapper.toJsonString(orderDto);

            response.setStatus(SC_CREATED);
            response.setContentType(JSON_CONTENT_TYPE);

            ServletUtils.writeResponse(response, orderDtoJson);
        } catch (BadRequestException e) {
            logger.error("Failed to create order due to bad request: {}", e.getMessage());
            response.setStatus(SC_BAD_REQUEST);
        } catch (IllegalStatusException e) {
            logger.error("Failed to create order. Scooter is already rented.", e);
            response.setStatus(SC_CONFLICT);

            ServletUtils.writeResponse(response, JsonErrorMessages.SCOOTER_IS_RENTED);
        } catch (DuplicateEntryException e) {
            logger.error("Failed to create order due to duplicate entry: {}", e.getMessage());
            response.setStatus(SC_CONFLICT);
        } catch (Exception e) {
            logger.error("Failed to create order with message: {}", e.getMessage());
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}
