import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScooterRepositoryTest extends BaseTest implements ScooterTestData {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);

    @Test
    public void saveScooterToDb() {
        //given
        final var scooter = aScooter().build();

        //when
        final var savedScooter = scooterRepository.save(scooter);
        final var scooterFromDb = scooterRepository.findById(scooter.getId()).get();

        //then
        assertEquals(savedScooter, scooterFromDb);
        assertNotNull(scooterFromDb.getUpdateTime());
        assertNotNull(scooterFromDb.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfAlreadyExists() {
        //given
        final var scooter = aScooter().build();

        //when
        scooterRepository.save(scooter);

        //then
        assertThrows(DuplicateEntryException.class, () -> scooterRepository.save(scooter));
    }

    @Test
    public void findByIdReturnsScooter() {
        final var scooter = aScooter().build();
        scooterRepository.save(scooter);

        final var scooterFromDb = scooterRepository.findById(scooter.getId()).get();

        assertEquals(scooter, scooterFromDb);
    }

    @Test
    public void findByStatusReturnsFullyList() {
        //given
        final var status = ScooterStatus.AVAILABLE;
        final var scooterList = List.of(
            aScooter().build(),
            aScooter().build()
        );
        scooterList.forEach(scooterRepository::save);

        //when
        final var scooterListFromDb = scooterRepository.findScootersByStatus(status);

        //then
        assertEquals(scooterList, scooterListFromDb);
    }

    @Test
    public void findAllReturnsFullyList() {
        //given
        final var scooterList = List.of(
            aScooter().build(),
            aScooter().build()
        );
        scooterList.forEach(scooterRepository::save);

        //when
        final var scootersFromDb = scooterRepository.findAll();

        //then
        assertEquals(scooterList, scootersFromDb);
    }

    @Test
    public void updatesScooterInDB() {
        //given
        final var newStatus = ScooterStatus.RENTED;
        final var scooter = aScooter().build();
        scooterRepository.save(scooter);
        final var updatedScooter = scooter.toBuilder().status(newStatus).updateTime(null).build();

        //when
        scooterRepository.update(updatedScooter);
        final var scooterFromDb = scooterRepository.findById(updatedScooter.getId()).get();


        //then
        assertEquals(updatedScooter, scooterFromDb);
        assertNotNull(scooterFromDb.getUpdateTime());
    }

    @Test
    public void deletesScooterFromDB() {
        //given
        final var scooter = aScooter().build();
        scooterRepository.save(scooter);

        //when
        final var result = scooterRepository.delete(scooter.getId());

        //then
        assertTrue(result);
    }

    @Test
    public void findAvailableScootersReturnsFullyList() {
        //given
        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(86).build(),
            aScooter().batteryLevel(72).build()
        );
        scooterList.forEach(scooterRepository::save);

        final var expectedList = scooterList.subList(2, 4);
        final var limit = 2;
        final var offset = 2;

        //when
        final var scootersFromDb = scooterRepository.findAvailableScootersForResponse(limit, offset);

        //then
        assertEquals(expectedList, scootersFromDb);
    }

    @Test
    public void getAvailableAmountReturnsAmount() {
        //given
        final var scooterList = List.of(
            aScooter().batteryLevel(100).build(),
            aScooter().batteryLevel(98).build(),
            aScooter().batteryLevel(60).status(ScooterStatus.RENTED).build()
        );
        scooterList.forEach(scooterRepository::save);

        final var expectedAmount = scooterList.size() - 1;

        //when
        final var amountFromDb = scooterRepository.getAvailableScootersAmount();

        //then
        assertEquals(expectedAmount, amountFromDb);
    }
}
