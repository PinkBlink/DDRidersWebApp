package org.riders.sharing.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class Command {
    public abstract void execute(HttpServletRequest request, HttpServletResponse response);
}
