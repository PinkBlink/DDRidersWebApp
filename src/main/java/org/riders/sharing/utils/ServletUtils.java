package org.riders.sharing.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.ResponseWritingException;

import java.io.IOException;

public class ServletUtils {
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        final var requestBodyBuilder = new StringBuilder();
        final var reader = request.getReader();
        var line = "";

        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }

        if (requestBodyBuilder.toString().isBlank()) {
            throw new BadRequestException("Attempt to get the body from empty request.");
        }

        return requestBodyBuilder.toString();
    }

    public static void writeResponse(HttpServletResponse response, String message) {
        try (final var writer = response.getWriter()) {
            writer.write(message);
        } catch (IOException e) {
            throw new ResponseWritingException("Couldn't write response.", e);
        }
    }
}
