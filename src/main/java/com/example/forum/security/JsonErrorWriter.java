package com.example.forum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

final class JsonErrorWriter {
    private JsonErrorWriter() {
    }

    static void write(HttpServletRequest request, HttpServletResponse response,
                      HttpStatus status, String message, String solution) throws IOException {
        String body = "{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status.value() + ","
                + "\"error\":\"" + escape(status.getReasonPhrase()) + "\","
                + "\"message\":\"" + escape(message) + "\","
                + "\"path\":\"" + escape(request.getRequestURI()) + "\","
                + "\"solution\":\"" + escape(solution) + "\","
                + "\"details\":[]"
                + "}";
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
