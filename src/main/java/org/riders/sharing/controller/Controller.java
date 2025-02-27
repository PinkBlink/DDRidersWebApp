package org.riders.sharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.dto.CustomerDTO;
import org.riders.sharing.exception.UserExistsException;
import org.riders.sharing.exception.WrongEmailOrPasswordException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.utils.PasswordEncryptor;
import org.riders.sharing.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "MainServlet", urlPatterns = "/main-servlet/*")
public class Controller extends HttpServlet {
    CustomerService customerService = new CustomerServiceImpl(new CustomerRepositoryImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo().equals("/login")) {
            String body = ServletUtils.getRequestBody(req);
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerDTO customerDTO = objectMapper.readValue(body, CustomerDTO.class);
            try {
                Customer customer = customerService.logIn(
                        customerDTO.getEmail(), PasswordEncryptor.hashPassword(customerDTO.getPassword()));
                resp.setContentType("text/html");
                resp.setStatus(200);
                resp.sendRedirect("/account.html");
                //need to finish
            } catch (WrongEmailOrPasswordException e) {
                resp.setStatus(401);
                resp.setContentType("application/json");
                resp.getWriter().println("{\"error\": \"Invalid login or password\"}");
            }
        }

        if (req.getPathInfo().equals("/registration")) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String body = ServletUtils.getRequestBody(req);

                CustomerDTO customerDTO = objectMapper.readValue(body, CustomerDTO.class);

                Customer newCustomer = Customer.Builder.getNewBuilderWithId()
                        .setName(customerDTO.getName())
                        .setSurname(customerDTO.getSurname())
                        .setEmail(customerDTO.getEmail())
                        .setPassword(PasswordEncryptor.hashPassword(customerDTO.getPassword()))
                        .build();

                customerService.register(newCustomer);

                resp.setStatus(201);
            } catch (UserExistsException e) {
                resp.setContentType("application/json");
                resp.setStatus(409);
                resp.getWriter().println("{\"error\" : \"User with this email already exists\"}");
            }
        }
    }
}
