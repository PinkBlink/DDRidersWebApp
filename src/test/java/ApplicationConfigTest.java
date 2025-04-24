import org.junit.jupiter.api.Test;
import org.riders.sharing.utils.ApplicationConfig;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ApplicationConfigTest {
    @Test
    public void getInstanceReturnsSingleton() {
        final var appConfig1 = ApplicationConfig.getInstance();

        final var appConfig2 = ApplicationConfig.getInstance();

        assertSame(appConfig1, appConfig2);
    }
}
