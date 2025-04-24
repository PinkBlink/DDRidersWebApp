package org.riders.sharing.utils.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.riders.sharing.exception.InvalidTokenException;
import org.riders.sharing.utils.ValidationUtils;

import java.util.Objects;

public class AuthTokenDecoder {
    private final Algorithm algorithm;

    public AuthTokenDecoder(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public DecodedJWT decode(String token) {
        ValidationUtils.checkThat(Objects.nonNull(token) && !token.isBlank(),
            () -> new InvalidTokenException("Token is empty or null"));

        return JWT.require(algorithm).build().verify(token);
    }
}
