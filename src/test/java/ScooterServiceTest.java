import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.ScooterServiceImpl;

import java.util.List;

public class ScooterServiceTest extends BaseTest implements ScooterTestData {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);

    @Test
    public void getAvailableReturnsScooters() {
        //given
        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(86).build(),
            aScooter().batteryLevel(72).build()
        );
        scooterList.forEach(scooterRepository::save);

        final var page = 2;
        final var pageSize = 2;
        final var totalElements = scooterList.size();
        final var totalPages = totalElements / pageSize;
        final var expectedList = scooterList.subList(2, totalElements)
            .stream()
            .map(ScooterDto::fromScooter)
            .toList();

        final var expectedPageResponse = new PageResponseDto<>(
            expectedList,
            page,
            pageSize,
            totalElements,
            totalPages);

        //when
        final var pageResponseFromDb = scooterService.getAvailableScooters(new PageRequestDto(page, pageSize));

        //then
        Assertions.assertEquals(expectedPageResponse, pageResponseFromDb);
    }
}
