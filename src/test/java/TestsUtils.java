import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.exception.DatabaseException;
import org.riders.sharing.model.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class TestsUtils {
    public static final String SCOOTERS_TABLE = "scooters";

    public static void deleteEntitiesFromDatabase(String tableName, BaseEntity... entities) throws SQLException {
        for (BaseEntity entity : entities) {
            deleteEntityFromDatabase(tableName, entity);
        }
    }

    public static void deleteEntityFromDatabase(String tableName, BaseEntity entity) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ConnectionPool connectionPool = ConnectionPool.INSTANCE;

        try {
            connection = connectionPool.getConnection();
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM %s WHERE id = ?".formatted(tableName));
            preparedStatement.setObject(1, entity.getId(), Types.OTHER);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DatabaseException("Something goes wrong.", e);
        } finally {
            connectionPool.releaseConnection(connection);
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
