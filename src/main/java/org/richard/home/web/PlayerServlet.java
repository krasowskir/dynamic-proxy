package org.richard.home.web;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.Player;
import org.richard.home.service.PlayerService;
import org.richard.home.web.dto.PlayerDTO;
import org.richard.home.web.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.richard.home.config.StaticApplicationConfiguration.VALIDATOR_FACTORY;
import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;
import static org.richard.home.web.WebConstants.HEADER_VALUE_FORM_URL_ENCODED;

// ToDo: könnte mit funktionaler Programmierung implementiert werden!
// ToDo: schreibe Tests für web layer!!!

//@WebServlet(urlPatterns = "/players/*")
public class PlayerServlet extends HttpServlet {

    private static final String REQ_PARAMETER_PLAYER_NAME = "name";

    private static final String PLAYERS_REQUEST_PATH = "/api/player";
    private static final Pattern pathPattern = Pattern.compile(PLAYERS_REQUEST_PATH);
    private static final Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private PlayerService playerService;
    private ObjectMapper objectMapper;

    public PlayerServlet() {
    }

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    private static String extractPlayerId(HttpServletRequest request) {
        return request.getRequestURI().split(PLAYERS_REQUEST_PATH)[1];
    }

    private static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    private static void validateAndHandleInvalid(PlayerDTO playerDTO) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(playerDTO);
        if (!errors.isEmpty()) {
            var errorMessagesCombined = errors.stream()
                    .map(error -> {
                        log.error("validation error: {}", error.getMessage());
                        return error.getMessage();
                    }).collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    private static void handleBadContentType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!request.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {
            log.error("tried to call uri: {} with invalid content type: {}", request.getRequestURI(), request.getContentType());
            handleResponse(response, SC_BAD_REQUEST, format("invalid content type used: %s", request.getContentType()));
        }
    }

    private static void handleInvalidPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!pathPattern.matcher(request.getRequestURI()).matches()) {
            handleResponse(response, SC_BAD_REQUEST, format("path used: %s is not appropriate!", request.getRequestURI()));
        }
    }

    private static String extractPlayerName(HttpServletRequest req) {
        var playerName = Objects.requireNonNull(req.getParameter(REQ_PARAMETER_PLAYER_NAME),
                "request parameter name cannot be null");
        var playerNameWithoutSlash = playerName.startsWith("/") ? playerName.substring(1) : playerName;
        return URLDecoder.decode(playerNameWithoutSlash, UTF_8);
    }

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");

//        this.playerService = Objects.requireNonNull(ContextLoaderListener.getCurrentWebApplicationContext()).getBean(PlayerService.class);
        this.playerService = StaticApplicationConfiguration.PLAYER_SERVICE_INSTANCE;
        this.objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleInvalidPath(req, resp);
        Player foundPlayer = null;

        try {
            if (req.getContentType() == null) {
                var playerName = extractPlayerName(req);
                var anotherPlayer = playerService.findPlayer(playerName);
                handleResponse(resp, SC_OK, objectMapper.writeValueAsString(anotherPlayer));
//                handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(anotherPlayer));
            } else {
                switch (req.getContentType()) {
                    case HEADER_VALUE_FORM_URL_ENCODED:
                        var playerName = extractPlayerName(req);
                        foundPlayer = playerService.findPlayer(playerName);
                        handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
//                        handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(foundPlayer));
                        break;
                    case HEADER_VALUE_APPLICATION_JSON:
                        String playerId = extractPlayerId(req);
                        Objects.requireNonNull(playerId.trim(), "required playerId in path is null!");
                        foundPlayer = playerService.findPlayerById(playerId);
                        handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
//                        handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(foundPlayer));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + req.getContentType());
                }
            }
        } catch (NullPointerException | IllegalStateException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (NoResultException e) {
            handleResponse(resp, SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleBadContentType(req, resp);
        PlayerDTO playerDTO = null;
        try {
            playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
            validateAndHandleInvalid(playerDTO);
            var player = PlayerMapper.fromWebLayerTo(playerDTO);
            player = playerService.savePlayer(player);
            handleResponse(resp, SC_CREATED, objectMapper.writeValueAsString(player));
        } catch (IllegalArgumentException e) {
            log.warn("invalid input provided for saving player: {}", playerDTO);
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleBadContentType(req, resp);
        String playerId = null;
        try {
            playerId = extractPlayerId(req);
            Objects.requireNonNull(playerId.trim(), "required playerId in path is null!");
            var playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
            validateAndHandleInvalid(playerDTO);
            var updatedPlayer = playerService.updatePlayerById(playerDTO, playerId);
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedPlayer));
        } catch (NullPointerException | DatabindException | IllegalArgumentException e) {
            log.warn("invalid input provided for updating of player: {}!", playerId);
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleInvalidPath(req, resp);
        String playerId = null;
        try {
            playerId = req.getRequestURI().split(PLAYERS_REQUEST_PATH)[1];
            if (playerService.deletePlayerById(playerId)) {
                handleResponse(resp, SC_OK, format("player: %s deleted successfully!", playerId));
            } else {
                handleResponse(resp, SC_BAD_REQUEST, format("player: %s could not be deleted successfully!", playerId));
            }
        } catch (IllegalStateException | PersistenceException e) {
            log.error("deleting of player: {} failed!", playerId);
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, format("failed to delete player: %s", playerId));
        }
    }
}
