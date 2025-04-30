import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.AvailableScootersCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.ScooterServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AvailableScootersCommandTest extends BaseTest implements ScooterTestData {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final AvailableScootersCommand availableScootersCommand = new AvailableScootersCommand(scooterService);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());

    @Test
    public void availableRespondsWith200AndScooters() throws IOException {
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);
        final var page = 3;
        final var pageSize = 3;
        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(86).build(),
            aScooter().batteryLevel(72).build(),
            aScooter().batteryLevel(71).build(),
            aScooter().batteryLevel(60).build(),
            aScooter().batteryLevel(60).build(),
            aScooter().batteryLevel(60).build()
        );
        scooterList.forEach(scooterRepository::save);
        final var jsonAsReader = new StringReader("""
            {
            "page" : "%s",
            "size" : "%s"
            }""".formatted(page, pageSize));
        final var requestReader = new BufferedReader(jsonAsReader);
        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);
        final var expectedResponseStatus = SC_OK;
        final var expectedScooterDtoList = new ArrayList<ScooterDto>();
        scooterList.subList(6, 8).forEach(scooter -> expectedScooterDtoList.add(ScooterDto.fromScooter(scooter)));
        final var expectedTotalElements = scooterList.size();
        final var expectedTotalPages = 3;
        final var expectedPageResponse = new PageResponseDto<ScooterDto>(expectedScooterDtoList, page, pageSize,
            expectedTotalElements, expectedTotalPages);


        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);
        availableScootersCommand.execute(request, response);

        verify(response).setStatus(expectedResponseStatus);
        PageResponseDto<ScooterDto> actualPageResponse = objectMapper.readValue(stringWriter.toString(),
            new TypeReference<>() {
            });
        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Test
    public void availableScootersRespondsWith400() throws IOException {
        final var jsonAsReader = new StringReader("");
        final var requestReader = new BufferedReader(jsonAsReader);
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);
        final var expectedResponseStatus = SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);
        availableScootersCommand.execute(request, response);

        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void availableRespondsWith404() throws IOException {
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);
        final var jsonAsReader = new StringReader("""
            {
            "page" : "42",
            "size" : "12"
            }""");
        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponseStatus = SC_NOT_FOUND;


        when(request.getReader()).thenReturn(requestReader);
        availableScootersCommand.execute(request, response);

        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void availableRespondsWith500() throws IOException {
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);
        final var expectedResponseStatus = SC_INTERNAL_SERVER_ERROR;


        when(request.getReader()).thenThrow(RuntimeException.class);
        availableScootersCommand.execute(request, response);

        verify(response).setStatus(expectedResponseStatus);
    }
}
