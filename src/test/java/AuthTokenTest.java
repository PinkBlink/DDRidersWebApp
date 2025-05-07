import org.junit.jupiter.api.Test;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.utils.ApplicationConfig;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.authentication.AuthTokenGenerator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthTokenTest implements CustomerTestData {
    private final ApplicationConfig appConfig = ApplicationConfig.getInstance();
    private final AuthTokenGenerator authTokenGenerator = new AuthTokenGenerator(
        appConfig.getAccessTokenTtl(),
        appConfig.getRefreshTokenTtl(),
        appConfig.getAlgorithm()
    );
    private final AuthTokenDecoder authTokenDecoder = new AuthTokenDecoder(appConfig.getAlgorithm());

    @Test
    public void generateReturnsToken() {
        //given
        final var customer = aCustomer().build();

        //when
        final var accessToken = authTokenGenerator.generateNewAccessToken(customer);
        final var refreshToken = authTokenGenerator.generateNewRefreshToken(customer);

        //then
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    public void decodesTokenThrowsBadRequstIfNullOrEmpty() {
        final var emptyToken = "";

        assertThrows(BadRequestException.class,
            () -> authTokenDecoder.decode(null));
        assertThrows(BadRequestException.class,
            () -> authTokenDecoder.decode(emptyToken));
    }

    @Test
    public void decodesToken() {
        //given
        final var customer = aCustomer().build();
        final var expectedId = customer.getId();
        final var expectedEmail = customer.getEmail();

        final var accessToken = authTokenGenerator.generateNewAccessToken(customer);
        final var refreshToken = authTokenGenerator.generateNewRefreshToken(customer);

        //when
        final var decodedAccess = authTokenDecoder.decode(accessToken);
        final var decodedRefresh = authTokenDecoder.decode(refreshToken);

        final var idFromAccess = UUID.fromString(decodedAccess.getSubject());
        final var idFromRefresh = UUID.fromString(decodedRefresh.getSubject());
        final var emailFromAccess = decodedAccess.getClaim("email").asString();

        //then
        assertEquals(expectedId, idFromAccess);
        assertEquals(expectedEmail, emailFromAccess);
        assertEquals(expectedId, idFromRefresh);
    }
}
