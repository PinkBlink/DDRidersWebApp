package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.utils.ModelMapper;
import org.riders.sharing.service.ScooterService;

import static org.riders.sharing.utils.ServletUtils.getRequestBody;
import static org.riders.sharing.utils.ServletUtils.writeResponse;

public class AvailableScootersCommand extends Command {
    private final ScooterService scooterService;

    public AvailableScootersCommand(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        final var requestBody = getRequestBody(request);
        final var pageRequestDto = ModelMapper.parse(requestBody, PageRequestDto.class);
        final var pageResponseDto = scooterService.getAvailableScooters(pageRequestDto);
        final var pageResponseAsJson = ModelMapper.toJsonString(pageResponseDto);

        response.setStatus(HttpServletResponse.SC_OK);

        writeResponse(response, pageResponseAsJson);
    }
}
