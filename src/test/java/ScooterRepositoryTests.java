import org.junit.jupiter.api.*;
import org.riders.sharing.exception.DuplicateEntryException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;


import java.util.*;

public class ScooterRepositoryTests {
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl();

    private static final Scooter SCOOTER_1 = ScooterTestData.aScooter().build();

    private static final Scooter SCOOTER_2 = ScooterTestData.aScooter().build();

    @BeforeAll
    public static void beforeAll() {
        TestsUtils.deleteEntitiesFromDatabase(TestsUtils.SCOOTERS_TABLE, SCOOTER_1, SCOOTER_2);
    }

    @AfterAll
    public static void afterAll() {
        TestsUtils.deleteEntitiesFromDatabase(TestsUtils.SCOOTERS_TABLE, SCOOTER_1, SCOOTER_2);
    }

    @AfterEach
    public void afterEach() {
        TestsUtils.deleteEntitiesFromDatabase(TestsUtils.SCOOTERS_TABLE, SCOOTER_1, SCOOTER_2);
    }

    @Test
    public void saveShouldSetCreateAndUpdateTime() {
        Scooter savedScooter = scooterRepository.save(SCOOTER_1);

        Assertions.assertTrue(savedScooter.getCreateTime().equals(savedScooter.getUpdateTime())
                && SCOOTER_1.equals(savedScooter));
    }

    @Test
    public void saveShouldThrowIfEqualsIds() {
        scooterRepository.save(SCOOTER_1);

        Assertions.assertThrows(DuplicateEntryException.class, () -> scooterRepository.save(SCOOTER_1));
    }

    @Test
    public void findShouldReturnScooter() {
        scooterRepository.save(SCOOTER_1);

        Scooter scooterFromDatabase = scooterRepository.findById(SCOOTER_1.getId()).get();

        Assertions.assertEquals(SCOOTER_1, scooterFromDatabase);
    }

    @Test
    public void findShouldReturnEmptyOptional() {
        Optional<Scooter> maybeScooter = scooterRepository.findById(UUID.randomUUID());

        Assertions.assertThrows(NoSuchElementException.class, maybeScooter::get);
    }

    @Test
    public void findScootersByStatusShouldReturnScooterList() {
        ScooterStatus status = ScooterStatus.AVAILABLE;
        List<Scooter> scooters = Arrays.asList(
                ScooterTestData.aScooter().build()
                , ScooterTestData.aScooter().build()
                , ScooterTestData.aScooter().build());

        scooters.forEach(scooterRepository::save);

        List<Scooter> scootersFromDatabase = scooterRepository.findScootersByStatus(status);

        Assertions.assertEquals(scooters, scootersFromDatabase);

        for (Scooter scooter : scootersFromDatabase) {
            TestsUtils.deleteEntityFromDatabase(TestsUtils.SCOOTERS_TABLE, scooter);
        }
    }

    @Test
    public void findScootersByStatusShouldReturnEmptyList() {
        scooterRepository.save(SCOOTER_1);

        List<Scooter> scooters = scooterRepository.findScootersByStatus(ScooterStatus.RENTED);

        Assertions.assertEquals(0, scooters.size());
    }

    @Test
    public void findAllShouldReturnAll() {
        scooterRepository.save(SCOOTER_1);
        scooterRepository.save(SCOOTER_2);

        List<Scooter> scootersFromDatabase = scooterRepository.findAll();

        Assertions.assertEquals(2, scootersFromDatabase.size());
    }

    @Test
    public void updateShouldReturnUpdatedScooter() {
        ScooterType newScooterType = ScooterType.URBAN;
        scooterRepository.save(SCOOTER_1);

        Scooter updatedScooter = SCOOTER_1.toBuilder()
                .setScooterType(newScooterType)
                .build();
        scooterRepository.update(updatedScooter);
        Scooter scooterFromDataBase = scooterRepository.findById(updatedScooter.getId()).get();

        Assertions.assertEquals(updatedScooter, scooterFromDataBase);
    }

    @Test
    public void updateShouldSetUpdateTime(){
        Scooter savedScooter = scooterRepository.save(SCOOTER_1);

        Scooter updatedScooter = scooterRepository.update(savedScooter);

        Assertions.assertTrue(savedScooter.getUpdateTime().isBefore(updatedScooter.getUpdateTime()));
    }

    @Test
    public void deleteShouldReturnTrue() {
        scooterRepository.save(SCOOTER_1);

        boolean result = scooterRepository.delete(SCOOTER_1.getId());

        Assertions.assertTrue(result);
    }

    @Test
    public void deleteShouldReturnFalse() {
        boolean result = scooterRepository.delete(SCOOTER_2.getId());

        Assertions.assertFalse(result);
    }
}
