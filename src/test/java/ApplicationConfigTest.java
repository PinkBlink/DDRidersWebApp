import org.junit.jupiter.api.Test;
import org.riders.sharing.config.ApplicationConfig;

import static org.junit.jupiter.api.Assertions.assertSame;

public class ApplicationConfigTest {
    @Test
    public void getInstanceReturnsSingleton() {
        //given
        final var appConfig1 = ApplicationConfig.getInstance();

        //when
        final var appConfig2 = ApplicationConfig.getInstance();

        //then
        assertSame(appConfig1, appConfig2);
    }
}
