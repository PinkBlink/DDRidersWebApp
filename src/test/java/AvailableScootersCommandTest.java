import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.riders.sharing.command.AvailableScootersCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.utils.ModelMapper;
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
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AvailableScootersCommandTest extends BaseTest implements ScooterTestData {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final AvailableScootersCommand availableScootersCommand = new AvailableScootersCommand(scooterService);

    @Test
    public void availableRespondsWith200AndScooters() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var page = 2;
        final var pageSize = 2;

        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(86).build(),
            aScooter().batteryLevel(72).build()
        );
        scooterList.forEach(scooterRepository::save);

        final var jsonAsReader = new StringReader("""
            {
            "page" : "%s",
            "pageSize" : "%s"
            }""".formatted(page, pageSize)
        );

        final var requestReader = new BufferedReader(jsonAsReader);
        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedScooterDtoList = scooterList
            .subList(2, 4)
            .stream()
            .map(ScooterDto::fromScooter)
            .toList();

        final var expectedTotalElements = scooterList.size();
        final var expectedTotalPages = 2;
        final var expectedPageResponse = new PageResponseDto<>(
            expectedScooterDtoList,
            page,
            pageSize,
            expectedTotalElements,
            expectedTotalPages
        );

        final var expectedResponseStatus = SC_OK;

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        availableScootersCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);

        final var actualPageResponse = ModelMapper.parse(
            stringWriter.toString(),
            new TypeReference<PageResponseDto<ScooterDto>>() {
            }
        );

        assertEquals(expectedPageResponse, actualPageResponse);
    }

    @Test
    public void availableScootersThrowsBadRequest() throws IOException {
        //given
        final var response = Mockito.mock(HttpServletResponse.class);
        final var request = Mockito.mock(HttpServletRequest.class);

        final var jsonAsReader = new StringReader("");
        final var requestReader = new BufferedReader(jsonAsReader);
        final var expectedResponseStatus = SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);

        //when & then
        Assertions.assertThrows(
            BadRequestException.class,
            () -> availableScootersCommand.execute(request, response)
        );
    }
}
