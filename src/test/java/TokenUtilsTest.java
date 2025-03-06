import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.riders.sharing.exception.ExpiredTokenException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.utils.TokenUtils;

import java.time.Instant;

public class TokenUtilsTest {
    private final Customer customer = Customer.Builder
            .getNewBuilderWithId()
            .setEmail("email@mail.com").build();

    @Test
    public void generateNewAccessTokenTest() {
        Assertions.assertFalse(TokenUtils.getNewAccessToken(customer).isEmpty());
    }

    @Test
    public void generateNewRefreshTokenTest() {
        Assertions.assertFalse(TokenUtils.getNewAccessToken(customer).isEmpty());
    }

    @Test
    public void testGetEncoderJWTAccessTokenDecodedIdShouldBeEquals() {

        String accessToken = TokenUtils.getNewAccessToken(customer);

        DecodedJWT decodedJWT = TokenUtils.getDecodedToken(accessToken);
        String decodedId = decodedJWT.getSubject();

        boolean result = decodedId.equals(customer.getId().toString());
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetDecodedJWTAccessTokenShouldDecodedEmailShouldBeEquals() {

        String accessToken = TokenUtils.getNewAccessToken(customer);

        DecodedJWT decodedJWT = TokenUtils.getDecodedToken(accessToken);
        String decodedEmail = decodedJWT.getClaim("email").toString().replaceAll("\"", "");

        Assertions.assertEquals(decodedEmail, customer.getEmail());
    }

    @Test
    public void testGetDecodedJWTRefreshTokenDecodedIdShouldBeEquals() {

        String refreshToken = TokenUtils.getNewRefreshToken(customer);

        DecodedJWT decodedJWT = TokenUtils.getDecodedToken(refreshToken);
        String decodedId = decodedJWT.getSubject();

        Assertions.assertEquals(customer.getId().toString(), decodedId);
    }

    @Test
    public void testIsTokenExpiredShouldNotThrow() {
        Instant notExpired = Instant.now().plusSeconds(10000);
        Assertions.assertDoesNotThrow(() -> TokenUtils.isTokenExpiredThrow(notExpired));
    }

    @Test
    public void testIsTokenExpiredShouldThrow() {
        Instant expired = Instant.now().minusSeconds(10000);
        Assertions.assertThrows(ExpiredTokenException.class, () -> TokenUtils.isTokenExpiredThrow(expired));
    }
}

