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

import java.util.ArrayList;
import java.util.List;

public class ScooterServiceTest extends BaseTest implements ScooterTestData {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);

    @Test
    public void getAvailableReturnsScooters() {
        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(86).build(),
            aScooter().batteryLevel(72).build(),
            aScooter().batteryLevel(71).build(),
            aScooter().batteryLevel(60).build()
        );
        scooterList.forEach(scooterRepository::save);
        final var page = 2;
        final var pageSize = 3;
        final var totalElements = scooterList.size();
        final var totalPages = totalElements / pageSize;
        final var expectedList = new ArrayList<ScooterDto>();
        scooterList.subList(3, scooterList.size())
            .forEach(scooter -> expectedList.add(ScooterDto.fromScooter(scooter)));
        final var expectedPageResponse = new PageResponseDto<>(expectedList, page, pageSize,
            totalElements, totalPages);

        final var pageResponseFromDb = scooterService.getAvailableScooters(new PageRequestDto(page, pageSize));

        Assertions.assertEquals(expectedPageResponse, pageResponseFromDb);
    }
}
