import org.junit.jupiter.api.BeforeEach;
import org.riders.sharing.exception.NoSQLConnectionException;
import org.riders.sharing.utils.constants.DatabaseInfo;

import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseTest {
    @BeforeEach
    public void cleanTables() {
        try (final var connection = DriverManager.getConnection(TestsConstants.TEST_DB_URL,
            DatabaseInfo.USER, DatabaseInfo.PASSWORD)) {

            try (final var statement = connection.prepareStatement(
                "TRUNCATE TABLE orders, scooters, customers")) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new NoSQLConnectionException(e.getMessage(), e);
        }
    }
}