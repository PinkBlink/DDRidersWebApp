package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class Command {
    public static final String JSON_CONTENT_TYPE = "application/json";

    public abstract void execute(HttpServletRequest request, HttpServletResponse response);
}
