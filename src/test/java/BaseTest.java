import org.junit.jupiter.api.BeforeAll;
import org.riders.sharing.connection.ConnectionPool;

public abstract class BaseTest {
    private static ConnectionPool connectionPoolForTests;

    @BeforeAll
    public static void initDB() {
        ConnectionPool.INSTANCE.setDatabaseURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "", "");
        connectionPoolForTests = ConnectionPool.INSTANCE;
    }

    public static void main(String[] args) {
        connectionPoolForTests.getConnection();
    }
}
