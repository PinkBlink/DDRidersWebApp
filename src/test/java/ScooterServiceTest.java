import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.ScooterServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.riders.sharing.model.enums.ScooterStatus.RENTED;

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
        assertEquals(expectedPageResponse, pageResponseFromDb);
    }

    @Test
    public void getByIdReturnsScooter() {
        //given
        final var savedScooter = scooterRepository.save(aScooter().build());

        //when
        final var scooterFromDb = scooterService.getById(savedScooter.getId());

        //then
        assertEquals(savedScooter, scooterFromDb);
    }

    @Test
    public void rentUpdatesScooterInDb() {
        //given
        final var savedScooter = scooterRepository.save(aScooter().build());

        final var expectedScooterStatus = RENTED;

        //when
        final var updatedScooter = scooterService.rentScooter(savedScooter);
        final var scooterFromDb = scooterService.getById(updatedScooter.getId());

        //then
        assertEquals(expectedScooterStatus, updatedScooter.getStatus());
        assertEquals(updatedScooter, scooterFromDb);
    }

    @Test
    public void rentScooterThrowsIllegalStatusIfAlreadyRented() {
        //given
        final var savedScooter = scooterRepository.save(
            aScooter()
                .status(RENTED)
                .build()
        );

        //then
        assertThrows(IllegalStatusException.class, () ->
            scooterService.rentScooter(savedScooter));
    }
}
