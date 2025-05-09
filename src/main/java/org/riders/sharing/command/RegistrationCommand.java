package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.CustomerDto;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.dto.RegistrationDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.utils.ServletUtils;

import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class RegistrationCommand extends Command {
    private final CustomerService customerService;

    public RegistrationCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = getRequestBody(request);
        final var registrationDto = ModelMapper.parse(requestBody, RegistrationDto.class);

        final var customer = customerService.register(registrationDto);
        final var customerDto = CustomerDto.fromCustomer(customer);
        final var customerJson = ModelMapper.toJsonString(customerDto);

        response.setStatus(HttpServletResponse.SC_CREATED);

        writeResponse(response, customerJson);
    }
}
