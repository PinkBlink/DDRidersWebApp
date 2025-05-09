package org.riders.sharing.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.riders.sharing.model.Customer;

import java.time.Instant;

import static org.riders.sharing.authentication.AuthConstants.EMAIL_CLAIM;

public class AuthTokenGenerator {
    private final int accessTtl;
    private final int refreshTtl;
    private final Algorithm algorithm;

    public AuthTokenGenerator(int accessTtl, int refreshTtl, Algorithm algorithm) {
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
        this.algorithm = algorithm;
    }

    public String generateNewAccessToken(Customer customer) {
        return buildToken(customer, accessTtl);
    }

    public String generateNewRefreshToken(Customer customer) {
        return buildToken(customer, refreshTtl);
    }

    private String buildToken(Customer customer, long ttlSeconds) {
        final var issuedAt = Instant.now();
        final var expiresAt = issuedAt.plusSeconds(ttlSeconds);

        return JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim(EMAIL_CLAIM, customer.getEmail())
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }
}
