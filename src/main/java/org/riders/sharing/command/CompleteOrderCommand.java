package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.exception.NotFoundException;
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
    private final OrderService orderService;

    public CompleteOrderCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = getRequestBody(request);
        final var orderDto = ModelMapper.parse(requestBody, OrderDto.class);

        final var completedOrder = orderService.completeOrder(orderDto);

        final var completedOrderDto = OrderDto.fromOrder(completedOrder);
        final var completedOrderDtoJson = ModelMapper.toJsonString(completedOrderDto);

        response.setStatus(SC_OK);
        response.setContentType(JSON_CONTENT_TYPE);

        writeResponse(response, completedOrderDtoJson);
    }
}
