import com.auth0.jwt.JWT;
import org.junit.jupiter.api.Test;
import org.riders.sharing.exception.BadTokenException;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.riders.sharing.utils.TokenUtils.ALGORITHM;
import static org.riders.sharing.utils.TokenUtils.decodeToken;
import static org.riders.sharing.utils.TokenUtils.generateNewAccessToken;
import static org.riders.sharing.utils.TokenUtils.generateNewRefreshToken;
import static org.riders.sharing.utils.TokenUtils.isActiveToken;


public class TokenUtilsTest implements CustomerTestData {
    @Test
    public void generateReturnsToken() {
        final var customer = aCustomer().build();

        final var accessToken = generateNewAccessToken(customer);
        final var refreshToken = generateNewRefreshToken(customer);

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    public void decodesTokenThrowBadTokenIfNullOrEmpty() {
        final String nullToken = null;
        final var emptyToken = "";

        assertThrows(BadTokenException.class,
            () -> decodeToken(nullToken));
        assertThrows(BadTokenException.class,
            () -> decodeToken(emptyToken));
    }

    @Test
    public void decodesToken() {
        final var customer = aCustomer().build();
        final var expectedId = customer.getId();
        final var expectedEmail = customer.getEmail();
        final var accessToken = generateNewAccessToken(customer);
        final var refreshToken = generateNewRefreshToken(customer);

        final var decodedAccess = decodeToken(accessToken);
        final var decodedRefresh = decodeToken(refreshToken);
        final var idFromAccess = UUID.fromString(decodedAccess.getSubject());
        final var idFromRefresh = UUID.fromString(decodedRefresh.getSubject());
        final var emailFromAccess = decodedAccess.getClaim("email").asString();

        assertEquals(expectedId, idFromAccess);
        assertEquals(expectedEmail, emailFromAccess);
        assertEquals(expectedId, idFromRefresh);
    }

    @Test
    public void isActiveReturnsTrue() {
        final var customer = aCustomer().build();

        final var accessToken = generateNewAccessToken(customer);
        final var refreshToken = generateNewRefreshToken(customer);

        assertTrue(isActiveToken(accessToken));
        assertTrue(isActiveToken(refreshToken));
    }

    @Test
    public void isActiveReturnsFalse() {
        final var customer = aCustomer().build();
        final var issuedAt = Instant.now().minusSeconds(3600);
        final var expiredAt = issuedAt.plusSeconds(10);
        final var expiredToken = JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim("email", customer.getEmail())
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiredAt)
            .sign(ALGORITHM);

        assertFalse(isActiveToken(expiredToken));
    }
}
