import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.model.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TestsUtils {
    public static final String SCOOTERS_TABLE = "scooters";

    public static void deleteEntitiesFromDatabase(String tableName, BaseEntity... entities){
        for (BaseEntity entity : entities) {
            deleteEntityFromDatabase(tableName, entity);
        }
    }

    public static void deleteEntityFromDatabase(String tableName, BaseEntity entity){
        ConnectionPool connectionPool = ConnectionPool.INSTANCE;
        final var connection = connectionPool.getConnection();

        try (final var preparedStatement = connection.prepareStatement(
                "DELETE FROM %s WHERE id = ?".formatted(tableName))) {

            preparedStatement.setObject(1, entity.getId(), Types.OTHER);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DatabaseException("Something goes wrong.", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }
}
