package org.riders.sharing.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.riders.sharing.exception.BadRequestException;

import java.io.IOException;

public class ServletUtils {
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        final var requestBodyBuilder = new StringBuilder();
        final var reader = request.getReader();
        var line = "";

        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }

        if (requestBodyBuilder.isEmpty()) {
            throw new BadRequestException("Attempt to get the body from empty request.");
        }

        return requestBodyBuilder.toString();
    }
}
