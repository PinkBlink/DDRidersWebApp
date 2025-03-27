package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.exception.BadRequestException;

import java.io.IOException;

public abstract class Command {
    public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public static Command defineCommand(HttpServletRequest request, HttpServletResponse response) {
        return switch (request.getPathInfo()) {

            case "/registration" -> new RegistrationCommand();
            case "/login" -> new LoginCommand();
            case "/logout" -> new LogoutCommand();
            case "/refresh" -> new RefreshCommand();

            case "/customer-info" -> new GetCustomerCommand();
            case "/available-scooters" -> new GetAvailableScootersCommand();
            case "/completed-orders" -> new GetCompletedCustomerOrdersCommand();
            case "/ongoing-orders" -> new GetOngoingCustomerOrders();

            case "/create-new-order" -> new CreateNewOrderCommand();
            case "/complete-order" -> new CompleteOrderCommand();

            default -> {
                throw new BadRequestException("Wrong path: " + request.getPathInfo());
            }
        };
    }
}
