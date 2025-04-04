import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.connection.DatabaseInitParams;
import org.riders.sharing.utils.constants.DatabaseInfo;

public interface TestsConstants {
    String PATH_TO_CREATE_DB_SCRIPT = "sql/create_db_script.sql";
    String PATH_TO_CREATE_TABLES_SCRIPT = "sql/create_tables_script.sql";
    String TEST_DB_URL = "jdbc:postgresql://localhost:5432/test_dd_riders_db";

    DatabaseInitParams TEST_DATABASE_INIT_PARAMS =
        new DatabaseInitParams(DatabaseInfo.POSTGRES_URL, TEST_DB_URL, PATH_TO_CREATE_DB_SCRIPT,
            PATH_TO_CREATE_TABLES_SCRIPT, DatabaseInfo.USER, DatabaseInfo.PASSWORD, TestsConstants.class);

    ConnectionPool TEST_CONNECTION_POOL = ConnectionPool.INSTANCE.setDatabaseInitParams(TEST_DATABASE_INIT_PARAMS);
}
