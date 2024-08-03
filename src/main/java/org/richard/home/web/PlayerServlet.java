package org.richard.home.web;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.Player;
import org.richard.home.service.PlayerService;
import org.richard.home.web.dto.PlayerDTO;
import org.richard.home.web.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.richard.home.web.WebConstants.*;

public class PlayerServlet extends HttpServlet {

    private static final String REQ_PARAMETER_PLAYER_NAME = "name";
    private static final Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private PlayerService playerService;
    private ObjectMapper objectMapper;
    private Validator validator;

    public PlayerServlet() {
    }

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    private static String parsePath(HttpServletRequest req, String path) {
        return req.getRequestURI().split(path)[1];
    }

    private static void handleNotAllowedPlayerIdInUriPath(HttpServletRequest req) {
        if (req.getRequestURI().split("/api/players").length > 0
                && !parsePath(req, "/api/players").equals("/")) {
            throw new IllegalStateException(String.format("If request parameters are used, no path parameters are allowed! URI path contianed a player id: %s. ", parsePath(req, "/api/players/")));
        }
    }

    private static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");

        this.playerService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(PlayerService.class);
        this.objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;
        this.validator = StaticApplicationConfiguration.VALIDATOR_FACTORY.getValidator();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (req.getHeader(HEADER_NAME_CONTENT_TYPE).equals(HEADER_VALUE_APPLICATION_JSON) &&
                    req.getRequestURI().startsWith("/api/players/")) {
                String playerId = parsePath(req, "/api/players/");
                Objects.requireNonNull(playerId.trim(), "required playerId in path is null!");
                Player foundPlayer = playerService.findPlayerById(playerId);
                handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
            } else if (req.getHeader(HEADER_NAME_CONTENT_TYPE).equals(HEADER_VALUE_FORM_URL_ENCODED)) {
                handleNotAllowedPlayerIdInUriPath(req);
                var playerName = Objects.requireNonNull(req.getParameter(REQ_PARAMETER_PLAYER_NAME), "request parameter name cannot be null");
                var foundPlayer = playerService.findPlayer(playerName);
                handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
            }
        } catch (NullPointerException | IllegalStateException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (NoResultException e) {
            handleResponse(resp, SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (req.getHeader(HEADER_NAME_CONTENT_TYPE).equals(HEADER_VALUE_APPLICATION_JSON)) {
            try {
                var playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
                String errorMessage = validator.validate(playerDTO).stream()
                        .filter(Objects::nonNull)
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(","));

                if (!errorMessage.isBlank()) {
                    log.error("player DTO in request failed validation!");
                    throw new IllegalArgumentException(errorMessage);
                }
                var player = PlayerMapper.fromWebLayerTo(playerDTO);
                player = playerService.savePlayer(player);
                handleResponse(resp, SC_CREATED, objectMapper.writeValueAsString(player));
            } catch (IllegalArgumentException e) {
                handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            if (req.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {

                String playerId = parsePath(req, "/api/players/");
                Objects.requireNonNull(playerId.trim(), "required playerId in path is null!");
                var playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
                var updatedPlayer = playerService.updatePlayerById(playerDTO, playerId);
                handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedPlayer));

            } else if (req.getContentType().equals(HEADER_VALUE_FORM_URL_ENCODED)) {

            }
        } catch (NullPointerException | DatabindException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    // ToDo: refactor! Natürlich startet die requestUri mit /api/players
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().startsWith("/api/players/")) {
            var playerId = parsePath(req, "/api/players/");
            if (playerService.deletePlayerById(playerId)) {
                handleResponse(resp, SC_OK, String.format("player: %s deleted successfully!", playerId));
            } else {
                handleResponse(resp, SC_BAD_REQUEST, String.format("player: %s could not be deleted successfully!", playerId));
            }
        }
    }
}
