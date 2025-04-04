package org.riders.sharing.connection;

public record DatabaseInitParams(String postgresDBUrl, String customDBUrl,
                                 String pathToCreateDBScript, String pathToCreateTablesScript,
                                 String user, String password, Class<?> contextClass) {
}
