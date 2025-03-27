package org.riders.sharing.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.ScooterDTO;
import org.riders.sharing.exception.NoAccessException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.ScooterServiceImpl;
import org.riders.sharing.utils.SecurityUtils;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetAvailableScootersCommand extends Command {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ScooterService scooterService = new ScooterServiceImpl(new ScooterRepositoryImpl());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SecurityUtils.hasCustomerAccessOrThrow(request);

            List<Scooter> scooterList = scooterService.getScooterListByStatus(ScooterStatus.AVAILABLE);
            List<ScooterDTO> scooterDTOList = scooterList.stream().map(ScooterDTO::parse).toList();

            response.setContentType("application/json");

            if (scooterList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                try (PrintWriter printWriter = response.getWriter()) {
                    printWriter.println(objectMapper.writeValueAsString(scooterDTOList));
                }
            }
        } catch (NoAccessException e) {
            logger.error(e);
            ServletUtils.handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }
}
