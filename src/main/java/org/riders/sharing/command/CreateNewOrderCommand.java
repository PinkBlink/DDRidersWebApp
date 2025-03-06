package org.riders.sharing.command;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.OrderDTO;
import org.riders.sharing.dto.ScooterDTO;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.exception.WrongStateException;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.OrderStatus;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.service.impl.ScooterServiceImpl;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;
import org.riders.sharing.utils.TokenUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.UUID;

public class CreateNewOrderCommand extends Command {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderService orderService = new OrderServiceImpl(new OrderRepositoryImpl());
        ScooterService scooterService = new ScooterServiceImpl(new ScooterRepositoryImpl());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            String token = TokenUtils.getAuthorizationToken(request);
            DecodedJWT decodedJWT = TokenUtils.getDecodedToken(token);

            UUID customerId = UUID.fromString(decodedJWT.getSubject());

            String body = ServletUtils.getRequestBody(request);
            ScooterDTO scooterDTO = objectMapper.readValue(body, ScooterDTO.class);
            Scooter scooter = scooterService.getById(scooterDTO.getId());

            if (scooter.getStatus() == ScooterStatus.RENTED) {
                throw new WrongStateException("Scooter with id %s is already rented"
                        .formatted(scooter.getId()));
            }

            Scooter scooterToStore = scooter.toBuilder()
                    .setStatus(ScooterStatus.RENTED)
                    .build();

            Order orderToStore = Order.Builder
                    .getNewBuilder()
                    .setCustomerId(customerId)
                    .setScooter(scooter)
                    .setStartTime(Instant.now())
                    .setOrderStatus(OrderStatus.ONGOING)
                    .build();

            OrderDTO orderDTOToResponse = OrderDTO.parse(orderToStore);

            scooterService.update(scooterToStore);
            orderService.saveOrder(orderToStore);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);

            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.println(objectMapper.writeValueAsString(orderDTOToResponse));
            }
        } catch (WrongStateException e) {
            logger.error(e.getMessage());

            ServletUtils.handleException(response, HttpServletResponse.SC_CONFLICT, e);
        } catch (NoAccessException e) {
            logger.error(e.getMessage());
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
