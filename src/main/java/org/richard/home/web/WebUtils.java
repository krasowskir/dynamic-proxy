package org.richard.home.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.web.dto.PlayerDTO;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.richard.home.config.StaticApplicationConfiguration.VALIDATOR_FACTORY;
import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;

public class WebUtils {

    public static String wrapIntoRegex(String input) {
        StringBuilder strB = new StringBuilder(input);
        return input.startsWith("/") ? strB.insert(0, ".*").toString() : input;
    }

    public static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    public static String extractPlayerId(HttpServletRequest request, String path) {
        return Pattern.compile("/api/players/.*").matcher(request.getRequestURI()).matches() ?
                request.getRequestURI().split(path)[1].substring(1) : null;

    }

    public static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    public static void validateAndHandleInvalid(PlayerDTO playerDTO) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(playerDTO);
        if (!errors.isEmpty()) {
            var errorMessagesCombined = errors.stream()
                    .map(error -> error.getMessage())
                    .collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    public static void handleBadContentType(HttpServletRequest request, HttpServletResponse response, Logger log) throws IOException {
        if (!request.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {
            log.error("tried to call uri: {} with invalid content type: {}", request.getRequestURI(), request.getContentType());
            handleResponse(response, SC_BAD_REQUEST, format("invalid content type used: %s", request.getContentType()));
        }
    }

    public static void handleInvalidPath(HttpServletRequest request, HttpServletResponse response, String validPath) throws IOException {
        if (!Pattern.matches(wrapIntoRegex(validPath), request.getRequestURI())) {
            throw new IllegalStateException(format("path used: %s is not appropriate!", request.getRequestURI()));
        }
    }

    public static String extractRequestParam(HttpServletRequest req, String requestParamName) {
        var playerName = Objects.requireNonNull(req.getParameter(requestParamName),
                "request parameter name cannot be null");
        var playerNameWithoutSlash = playerName.startsWith("/") ? playerName.substring(1) : playerName;
        return URLDecoder.decode(playerNameWithoutSlash, UTF_8);
    }

    public static String stripCharset(String contentType) {
        return contentType == null ? null : contentType.replaceAll("; charset=.*", "");
    }
}
