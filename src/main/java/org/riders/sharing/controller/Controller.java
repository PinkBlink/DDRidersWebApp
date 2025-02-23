package org.riders.sharing.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DDRidersServlet", urlPatterns = "/main-servlet/*")
public class Controller extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("text/plain");
        if (path == null || path.equals("/")){
            resp.getWriter().println("HELLO DIRTY DICKS");
        }else if(path.equals("/forever")){
            resp.getWriter().println("OF COURSE FOREVER");
        }else{
            resp.sendError(404);
        }
    }
}