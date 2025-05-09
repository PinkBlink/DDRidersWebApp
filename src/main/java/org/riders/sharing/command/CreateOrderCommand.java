package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CreateOrderDto;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.utils.ModelMapper;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class CreateOrderCommand extends Command {
    private final OrderService orderService;

    public CreateOrderCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
            final var requestBody = getRequestBody(request);
            final var createOrderDto = ModelMapper.parse(requestBody, CreateOrderDto.class);

            final var order = orderService.createOrder(createOrderDto);
            final var orderDto = OrderDto.fromOrder(order);
            final var orderDtoJson = ModelMapper.toJsonString(orderDto);

            response.setStatus(SC_CREATED);
            response.setContentType(JSON_CONTENT_TYPE);

            writeResponse(response, orderDtoJson);
    }
}
