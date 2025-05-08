package org.riders.sharing.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.utils.ValidationUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.riders.sharing.authentication.AuthConstants.AUTH_HEADER;
import static org.riders.sharing.authentication.AuthConstants.BEARER;
import static org.riders.sharing.authentication.AuthConstants.EMAIL_CLAIM;
import static org.riders.sharing.authentication.AuthConstants.EMPTY_STRING;

public class AuthTokenDecoder {
    private final Algorithm algorithm;

    public AuthTokenDecoder(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public DecodedJWT decode(String token) {
        ValidationUtils.checkThat(
            Objects.nonNull(token) && !token.isBlank(),
            () -> new BadRequestException("Token is empty.")
        );

        return JWT.require(algorithm).build().verify(token);
    }

    public UUID getIdFromToken(DecodedJWT decodedToken) {
        try {
            return UUID.fromString(decodedToken.getSubject());
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Couldn't parse subject from token to UUID.", e);
        }
    }

    public String getEmailFromAccessToken(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(EMAIL_CLAIM))
            .map(Claim::asString)
            .orElseThrow(
                () -> new InvalidTokenException("Claim %s is missing".formatted(EMAIL_CLAIM))
            );
    }


    public String getAccessTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_HEADER))
            .map(header -> header.replace(BEARER, EMPTY_STRING).trim())
            .orElseThrow(() ->
                new BadRequestException("Header %s is missing.".formatted(AUTH_HEADER))
            );
    }
}
