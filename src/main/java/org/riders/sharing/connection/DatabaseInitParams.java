package org.riders.sharing.connection;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public record DatabaseInitParams(String postgresDBUrl, String customDBUrl,
                                 String pathToCreateDBScript, String pathToCreateTablesScript,
                                 String user, String password) {

    public static DatabaseInitParams getFromConfig() {
        final var yaml = new Yaml();

        try (final var input = DatabaseInitParams.class.getClassLoader().getResourceAsStream("config.yml")) {
            Map<String, String> config = yaml.load(input);

            return new DatabaseInitParams(
                config.get("postgres_url"),
                config.get("dd_riders_db_url"),
                config.get("path_to_create_db_script"),
                config.get("path_to_create_tables_script"),
                config.get("user"),
                config.get("password")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YML config", e);
        }
    }
}
