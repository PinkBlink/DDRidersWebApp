package org.riders.sharing.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.exception.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletUtils {
    public static String getRequestBody(HttpServletRequest request) throws IOException {

        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        if (requestBody.isEmpty()) {
            throw new BadRequestException("Request without body");
        }
        return requestBody.toString();
    }

    public static String getErrorMessageAsJson(Exception e) {
        return "{\"error\": \"%s\"}".formatted(e.getMessage());
    }

    public static void handleException(HttpServletResponse response, int code, Exception e) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.println(ServletUtils.getErrorMessageAsJson(e));
        }
    }
}
