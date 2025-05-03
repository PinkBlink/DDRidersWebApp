import org.junit.jupiter.api.BeforeEach;
import org.riders.sharing.exception.NoSQLConnectionException;
import org.riders.sharing.utils.ApplicationConfig;

import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseTest {
    private final ApplicationConfig applicationConfig = ApplicationConfig.getInstance();

    @BeforeEach
    public void cleanBefore() {
        cleanTables();
    }

    private void cleanTables(){
        deleteFromTable("orders");
        deleteFromTable("scooters");
        deleteFromTable("customers");
    }

    private void deleteFromTable(String table) {

        try (final var connection = DriverManager.getConnection(
            applicationConfig.getDdRidersDbUrl(),
            applicationConfig.getUser(),
            applicationConfig.getPassword())
        ) {
            try (final var statement = connection.prepareStatement(
                "DELETE FROM %s".formatted(table))) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new NoSQLConnectionException(e.getMessage(), e);
        }
    }
}
