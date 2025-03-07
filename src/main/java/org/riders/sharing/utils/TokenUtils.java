package org.riders.sharing.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.BadTokenException;
import org.riders.sharing.exception.ExpiredTokenException;
import org.riders.sharing.model.Customer;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.*;

import java.time.Instant;
import java.util.Arrays;

public class TokenUtils {
    private static final Logger logger = LogManager.getLogger(TokenUtils.class);
    private static final String SIGNATURE_SECRET = "dirtydridersforever";

    public static String getNewAccessToken(Customer customer) {
        Algorithm algorithm = Algorithm.HMAC256(SIGNATURE_SECRET);
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(60000);

        return JWT.create()
                .withSubject(customer.getId().toString())
                .withClaim("email", customer.getEmail())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public static String getNewRefreshToken(Customer customer) {
        Algorithm algorithm = Algorithm.HMAC256(SIGNATURE_SECRET);
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(6000);

        return JWT.create()
                .withSubject(customer.getId().toString())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public static DecodedJWT getDecodedToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SIGNATURE_SECRET);
        JWTVerifier verifier = JWT
                .require(algorithm)
                .build();
        return verifier.verify(token);
    }

    public static String getAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.equals("null") || token.isBlank()) {
            logger.warn("Accept token '%s' to method getDecodedJWT".formatted(token));
            throw new BadTokenException("Token %s doesn't exist".formatted(token));
        }
        return token.replace("Bearer", "").trim();
    }

    public static String getRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .orElseThrow(() -> new BadTokenException("Refresh token doesn't exists"))
                .getValue();
    }

    public static void isTokenExpiredThrow(Instant expiresAt) {
        Instant currentMoment = Instant.now();
        if (currentMoment.isAfter(expiresAt) || currentMoment.equals(expiresAt)) {
            throw new ExpiredTokenException("Token is expired : " + expiresAt);
        }
    }
}
