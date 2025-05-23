package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CustomerOrdersRequestDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.utils.ServletUtils;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class CompletedCustomerOrdersCommand extends Command {
    private final Logger logger = LogManager.getLogger(CompletedCustomerOrdersCommand.class);
    private final OrderService orderService;

    public CompletedCustomerOrdersCommand(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var customerOrdersRequestDto = ModelMapper.parse(requestBody, CustomerOrdersRequestDto.class);

            final var pageResponseDto = orderService.getCompletedCustomerOrders(customerOrdersRequestDto);
            final var responseJson = ModelMapper.toJsonString(pageResponseDto);

            response.setStatus(SC_OK);
            response.setContentType(JSON_CONTENT_TYPE);

            writeResponse(response, responseJson);
        } catch (BadRequestException e) {
            logger.error("Failed due to bad request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed with message: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
