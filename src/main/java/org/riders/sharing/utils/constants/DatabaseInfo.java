package org.riders.sharing.utils.constants;

import org.riders.sharing.connection.DatabaseInitParams;

public class DatabaseInfo {
    public static final String USER = "postgres";
    public static final String PASSWORD = "pinkblink";
    public static final String DD_RIDERS_URL = "jdbc:postgresql://localhost:5432/dd_riders_db";
    public static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String PATH_TO_CREATE_DATABASE_FILE = "sql/create_db_script.sql";
    public static final String PATH_TO_CREATE_TABLES_FILE = "sql/create_tables_script.sql";

    public static final DatabaseInitParams DD_RIDERS_DATABASE_INIT_PARAMS = new DatabaseInitParams(
        POSTGRES_URL, DD_RIDERS_URL, PATH_TO_CREATE_DATABASE_FILE, PATH_TO_CREATE_TABLES_FILE, USER,
        PASSWORD, DatabaseInfo.class);
}
