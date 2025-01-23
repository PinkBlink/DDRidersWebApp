import org.junit.jupiter.api.Test;
import org.riders.sharing.connection.ConnectionPull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionPullTests {
    @Test
    public void ConnectionPullTest() throws SQLException {
        ConnectionPull connectionPull = ConnectionPull.getInstance();
        Connection connection = connectionPull.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO Customer(id) VALUES(1)");
        statement.close();
    }
}
