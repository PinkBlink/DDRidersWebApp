package org.riders.sharing.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.command.Command;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import java.io.IOException;

@WebServlet(name = "MainServlet", urlPatterns = "/main-servlet/*")
public class Controller extends HttpServlet {
    CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = Command.defineCommand(req);
        command.execute(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = Command.defineCommand(req);
        command.execute(req, resp);
    }
}
