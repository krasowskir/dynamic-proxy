package org.richard.home.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.richard.home.config.StaticApplicationConfiguration.VALIDATOR_FACTORY;

public class WebUtils {

    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    private static final String PLAYERS_UNDER_CONTRACT_PATH = ".*/contracts";

    private static Pattern PLAYER_UNDER_CONTRACT_PLAYER_ID = Pattern.compile(PLAYERS_UNDER_CONTRACT_PATH);
    public static Function<HttpServletRequest, String> extractPlayerIdFrom = (HttpServletRequest request) ->
            Optional.of(PLAYER_UNDER_CONTRACT_PLAYER_ID.matcher(request.getRequestURI()))
                    .filter(Matcher::matches)
                    .map(matcher -> matcher.group(1))
                    .map(elem -> elem.replaceAll("/contract", ""))
                    .orElse(null);

    public WebUtils() {

    }

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
                Optional.of(request.getRequestURI().split(path)[1].substring(1))
                        .stream()
                        .takeWhile(elem -> !elem.isBlank())
                        .findFirst()
                        .orElse(null) : null;
    }

    public static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    public static void validateAndHandleInvalid(Object genericDTO) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(genericDTO);
        if (!errors.isEmpty()) {
            var errorMessagesCombined = errors.stream()
                    .map(error -> error.getMessage())
                    .collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    public static void handleBadContentType(HttpServletRequest request, String expectedContentType) {
        if (!stripCharset(request.getContentType()).equals(expectedContentType)) {
            log.warn("tried to call uri: {} with invalid content type: {}", request.getRequestURI(), request.getContentType());
            throw new IllegalArgumentException(format("tried to call uri: %s with invalid content type: %s", request.getRequestURI(), request.getContentType()));
        }
    }

    public static void handleInvalidPath(HttpServletRequest request, String validPath) throws IOException {
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
