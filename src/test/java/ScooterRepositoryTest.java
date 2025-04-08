import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScooterRepositoryTest extends BaseTest {
    private static final Scooter SCOOTER_1 = ScooterTestData.aScooter().build();
    private static final Scooter SCOOTER_2 = ScooterTestData.aScooter().build();

    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);

    @Test
    public void saveSetCreateAndUpdateTime() {
        final var savedScooter = scooterRepository.save(SCOOTER_1);

        assertTrue(savedScooter.getCreateTime().equals(savedScooter.getUpdateTime())
            && SCOOTER_1.equals(savedScooter));
    }

    @Test
    public void saveThrowsDuplicateEntryIfAlreadyExists() {
        scooterRepository.save(SCOOTER_1);

        assertThrows(DuplicateEntryException.class, () -> scooterRepository.save(SCOOTER_1));
    }

    @Test
    public void findByIdReturnsScooter() {
        scooterRepository.save(SCOOTER_1);

        final var scooterFromDatabase = scooterRepository.findById(SCOOTER_1.getId()).get();

        assertEquals(SCOOTER_1, scooterFromDatabase);
    }

    @Test
    public void findByIdReturnsEmptyOptional() {
        final var maybeScooter = scooterRepository.findById(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, maybeScooter::get);
    }

    @Test
    public void findByStatusReturnsFullyList() {
        final var status = ScooterStatus.AVAILABLE;
        final var scooters = List.of(ScooterTestData.aScooter().build(),
            ScooterTestData.aScooter().build(),
            ScooterTestData.aScooter().build());

        scooters.forEach(scooterRepository::save);

        final var scootersFromDatabase = scooterRepository.findScootersByStatus(status);

        assertEquals(scooters, scootersFromDatabase);
    }

    @Test
    public void findByStatusShouldReturnsEmptyList() {
        scooterRepository.save(SCOOTER_1);

        final var scooters = scooterRepository.findScootersByStatus(ScooterStatus.RENTED);

        assertEquals(0, scooters.size());
    }

    @Test
    public void findAllReturnsFullyList() {
        scooterRepository.save(SCOOTER_1);
        scooterRepository.save(SCOOTER_2);

        final var scootersFromDatabase = scooterRepository.findAll();

        assertEquals(2, scootersFromDatabase.size());
    }

    @Test
    public void updatesScooterInDB() {
        final var newScooterType = ScooterType.URBAN;
        scooterRepository.save(SCOOTER_1);

        final var updatedScooter = SCOOTER_1.toBuilder().type(newScooterType).build();

        scooterRepository.update(updatedScooter);
        final var scooterFromDatabase = scooterRepository.findById(updatedScooter.getId()).get();

        assertEquals(updatedScooter, scooterFromDatabase);
    }

    @Test
    public void updatesSetUpdateTime() {
        scooterRepository.save(SCOOTER_1);

        final var updatedScooter = scooterRepository.update(SCOOTER_1.toBuilder().updateTime(null).build());

        assertNotNull(updatedScooter.getUpdateTime());
    }

    @Test
    public void deletesScooterFromDB() {
        scooterRepository.save(SCOOTER_1);

        final var result = scooterRepository.delete(SCOOTER_1.getId());

        assertTrue(result);
    }
}
