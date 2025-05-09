package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ChangePasswordDto;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.UnauthorizedException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class ChangePasswordCommand extends Command {
    private final CustomerService customerService;

    public ChangePasswordCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = getRequestBody(request);
        final var changePassDto = ModelMapper.parse(requestBody, ChangePasswordDto.class);

        final var updatedCustomer = customerService.changePassword(changePassDto);
        final var customerDtoToResponse = CustomerDto.fromCustomer(updatedCustomer);
        final var jsonCustomerDto = ModelMapper.toJsonString(customerDtoToResponse);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType(JSON_CONTENT_TYPE);

        writeResponse(response, jsonCustomerDto);
    }
}
