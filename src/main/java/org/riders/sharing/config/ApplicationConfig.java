package org.riders.sharing.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.riders.sharing.exception.ConfigLoadException;
import org.riders.sharing.utils.ModelMapper;

import java.io.IOException;

import static org.riders.sharing.utils.ErrorMessages.ERROR_LOADING_CONFIG;

public class ApplicationConfig {
    private final String postgresDbUrl;
    private final String ddRidersDbUrl;
    private final String user;
    private final String password;
    private final String pathToCreateDbScript;
    private final String pathToCreateTablesScript;
    private final int accessTokenTtl;
    private final int refreshTokenTtl;
    private final String signature;
    @JsonIgnore
    private final Algorithm algorithm;

    private static ApplicationConfig instance;

    @JsonCreator
    private ApplicationConfig(
        @JsonProperty("postgresDbUrl") String postgresDbUrl,
        @JsonProperty("ddRidersDbUrl") String ddRidersDbUrl,
        @JsonProperty("user") String user,
        @JsonProperty("password") String password,
        @JsonProperty("pathToCreateDbScript") String pathToCreateDbScript,
        @JsonProperty("pathToCreateTablesScript") String pathToCreateTablesScript,
        @JsonProperty("accessTokenTtl") int accessTokenTtl,
        @JsonProperty("refreshTokenTtl") int refreshTokenTtl,
        @JsonProperty("signature") String signature
    ) {
        this.postgresDbUrl = postgresDbUrl;
        this.ddRidersDbUrl = ddRidersDbUrl;
        this.user = user;
        this.password = password;
        this.pathToCreateDbScript = pathToCreateDbScript;
        this.pathToCreateTablesScript = pathToCreateTablesScript;
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
        this.signature = signature;
        this.algorithm = Algorithm.HMAC256(signature);
    }

    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = initFromConfig();
        }
        return instance;
    }

    public String getPostgresDbUrl() {
        return postgresDbUrl;
    }

    public String getDdRidersDbUrl() {
        return ddRidersDbUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getPathToCreateDbScript() {
        return pathToCreateDbScript;
    }

    public String getPathToCreateTablesScript() {
        return pathToCreateTablesScript;
    }

    public int getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public int getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public String getSignature() {
        return signature;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    private static ApplicationConfig initFromConfig() {
        try (final var input = ApplicationConfig.class.getClassLoader().getResourceAsStream("config.yml")) {
            return ModelMapper.parse(input, ApplicationConfig.class);
        } catch (IOException e) {
            throw new ConfigLoadException(ERROR_LOADING_CONFIG, e);
        }
    }
}
