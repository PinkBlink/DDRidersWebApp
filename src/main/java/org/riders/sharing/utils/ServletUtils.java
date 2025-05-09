package org.riders.sharing.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.ResponseWritingException;

import java.io.BufferedReader;
import java.io.IOException;

import static org.riders.sharing.utils.ErrorMessages.REQUEST_IS_EMPTY;

public class ServletUtils {
    public static String getRequestBody(HttpServletRequest request) {
        final var requestBodyBuilder = new StringBuilder();


        final BufferedReader reader;
        try {
            reader = request.getReader();

            var line = "";

            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
        } catch (IOException e) {
            throw new ResponseWritingException(e.getMessage(), e);
        }

        if (requestBodyBuilder.toString().isBlank()) {
            throw new BadRequestException(REQUEST_IS_EMPTY);
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
