package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ModelMapper;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.ServletUtils;

public class AvailableScootersCommand extends Command {
    private final Logger logger = LogManager.getLogger(AvailableScootersCommand.class);
    private final ModelMapper modelMapper = ModelMapper.INSTANCE;
    private final ScooterService scooterService;

    public AvailableScootersCommand(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var requestBody = ServletUtils.getRequestBody(request);
            final var pageRequestDto = modelMapper.getAsObject(requestBody, PageRequestDto.class);
            final var pageResponseDto = scooterService.getAvailableScooters(pageRequestDto);
            final var pageResponseAsJson = modelMapper.getAsJson(pageResponseDto);

            response.setStatus(HttpServletResponse.SC_OK);

            try (final var writer = response.getWriter()) {
                writer.write(pageResponseAsJson);
            }
        } catch (BadRequestException e) {
            logger.error("Failed due to bad request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed with message: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
