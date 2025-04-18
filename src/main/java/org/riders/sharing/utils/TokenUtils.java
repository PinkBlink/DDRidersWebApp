package org.riders.sharing.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import org.riders.sharing.exception.BadTokenException;
import org.riders.sharing.model.Customer;

import java.time.Instant;
import java.util.Objects;

public class TokenUtils {
    private static final String SIGNATURE_SECRET = "dirtydridersforever";
    private static final int ACCESS_TOKEN_SECONDS = 600;
    private static final int REFRESH_TOKEN_SECONDS = 6000;

    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SIGNATURE_SECRET);

    public static String generateNewAccessToken(Customer customer) {
        final var issuedAt = Instant.now();
        final var expiresAt = issuedAt.plusSeconds(ACCESS_TOKEN_SECONDS);

        return JWT.create()
            .withSubject(customer.getId().toString())
            .withClaim("email", customer.getEmail())
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt)
            .sign(ALGORITHM);
    }

    public static String generateNewRefreshToken(Customer customer) {
        final var issuedAt = Instant.now();
        final var expiresAt = issuedAt.plusSeconds(REFRESH_TOKEN_SECONDS);

        return JWT.create()
            .withSubject(customer.getId().toString())
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt)
            .sign(ALGORITHM);
    }

    public static DecodedJWT decodeToken(String token) {
        ValidationUtils.checkThat(Objects.nonNull(token) && !token.isBlank()
            , () -> new BadTokenException("Token is empty or null"));
        return JWT.require(ALGORITHM).build().verify(token);
    }

    public static Cookie getCookieWithRefreshToken(Customer customer) {
        final var refreshToken = generateNewRefreshToken(customer);
        final var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/refresh");
        cookie.setMaxAge(REFRESH_TOKEN_SECONDS);

        return cookie;
    }

    public static boolean isActiveToken(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (TokenExpiredException e) {
            return false;
        }
    }

    public static String extractToken(String string){
        return string.replace("Bearer","").trim();
    }
}
