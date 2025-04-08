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
        final var scooter = aScooter().build();

        final var savedScooter = scooterRepository.save(scooter);
        final var scooterFromDb = scooterRepository.findById(scooter.getId()).get();

        assertEquals(savedScooter, scooterFromDb);
        assertNotNull(scooterFromDb.getUpdateTime());
        assertNotNull(scooterFromDb.getCreateTime());
    }

    @Test
    public void saveThrowsDuplicateEntryIfAlreadyExists() {
        final var scooter = aScooter().build();

        scooterRepository.save(scooter);

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
        final var status = ScooterStatus.AVAILABLE;
        final var scooterList = List.of(
            aScooter().build(),
            aScooter().build());
        scooterList.forEach(scooterRepository::save);

        final var scooterListFromDb = scooterRepository.findScootersByStatus(status);

        assertEquals(scooterList, scooterListFromDb);
    }

    @Test
    public void findAllReturnsFullyList() {
        final var scooterList = List.of(
            aScooter().build(),
            aScooter().build());
        scooterList.forEach(scooterRepository::save);

        final var scootersFromDb = scooterRepository.findAll();

        assertEquals(scooterList, scootersFromDb);
    }

    @Test
    public void updatesScooterInDB() {
        final var newStatus = ScooterStatus.RENTED;
        final var scooter = aScooter().build();
        scooterRepository.save(scooter);

        final var updatedScooter = scooter.toBuilder().status(newStatus).updateTime(null).build();
        scooterRepository.update(updatedScooter);
        final var scooterFromDb = scooterRepository.findById(updatedScooter.getId()).get();

        assertEquals(updatedScooter, scooterFromDb);
        assertNotNull(scooterFromDb.getUpdateTime());
    }

    @Test
    public void deletesScooterFromDB() {
        final var scooter = aScooter().build();
        scooterRepository.save(scooter);

        final var result = scooterRepository.delete(scooter.getId());

        assertTrue(result);
    }
}
