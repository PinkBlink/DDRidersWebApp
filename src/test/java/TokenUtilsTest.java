import com.auth0.jwt.JWT;
import org.junit.jupiter.api.Test;
import org.riders.sharing.exception.BadTokenException;
import org.riders.sharing.utils.TokenUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.riders.sharing.utils.TokenUtils.*;

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
            () -> TokenUtils.decodeToken(nullToken));
        assertThrows(BadTokenException.class,
            () -> TokenUtils.decodeToken(emptyToken));
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

        assertTrue(TokenUtils.isActiveToken(accessToken));
        assertTrue(TokenUtils.isActiveToken(refreshToken));
    }

    @Test
    public void isActiveReturnsFalse() {
        final var customer = aCustomer().build();
        Instant issuedAt = Instant.now().minusSeconds(3600);
        Instant expiredAt = issuedAt.plusSeconds(10);
        String expiredToken = JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim("email", customer.getEmail())
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiredAt)
            .sign(ALGORITHM);

        assertFalse(isActiveToken(expiredToken));
    }
}
