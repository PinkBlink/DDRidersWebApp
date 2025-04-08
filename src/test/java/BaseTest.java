import org.junit.jupiter.api.BeforeEach;
import org.riders.sharing.connection.DatabaseInitParams;
import org.riders.sharing.exception.NoSQLConnectionException;

import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseTest {
    private final DatabaseInitParams databaseInitParams = DatabaseInitParams.getFromConfig();

    @BeforeEach
    public void clanTables() {
        deleteFromTable("orders");
        deleteFromTable("scooters");
        deleteFromTable("customers");
    }

    private void deleteFromTable(String table) {

        try (final var connection = DriverManager.getConnection(databaseInitParams.customDBUrl(),
            databaseInitParams.user(), databaseInitParams.password())) {
            try (final var statement = connection.prepareStatement(
                "DELETE FROM %s".formatted(table))) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new NoSQLConnectionException(e.getMessage(), e);
        }
    }
}
