package org.richard.home.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
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

import java.io.IOException;
import java.util.Objects;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.lang.String.format;
import static org.richard.home.web.WebConstants.*;
import static org.richard.home.web.WebUtils.*;

// ToDo: könnte mit funktionaler Programmierung implementiert werden!
public class PlayerServlet extends HttpServlet {

    private static final String PLAYERS_REQUEST_PATH = "/api/players";
    private static final Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private PlayerService playerService;
    private ObjectMapper objectMapper;

    public PlayerServlet() {
    }

    @Override
    public void init() {
        log.info("init method without args was called...");
        this.playerService = StaticApplicationConfiguration.PLAYER_SERVICE_INSTANCE;
        this.objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // ToDo: getPlayer by age is missing!
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Player foundPlayer = null;
        try {
            switch (stripCharset(req.getContentType())) {
                case null, HEADER_VALUE_FORM_URL_ENCODED -> {
                    handleInvalidPath(req, PLAYERS_REQUEST_PATH);
                    var playerName = extractRequestParam(req, REQ_PARAMETER_NAME);
                    foundPlayer = playerService.findPlayer(playerName);
                    handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
                }
                case HEADER_VALUE_APPLICATION_JSON -> {
                    String playerId = extractPlayerId(req, PLAYERS_REQUEST_PATH);
                    Objects.requireNonNull(playerId.trim(), "required playerId in path is null!");
                    foundPlayer = playerService.findPlayerById(playerId);
                    handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundPlayer));
                }
                default -> throw new IllegalStateException("Unexpected value: " + req.getContentType());
            }
        } catch (NullPointerException | IllegalStateException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (NoResultException e) {
            handleResponse(resp, SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PlayerDTO playerDTO = null;
        try {
            handleBadContentType(req, HEADER_VALUE_APPLICATION_JSON);
            playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
            validateAndHandleInvalid(playerDTO);
            var player = PlayerMapper.fromWebLayerTo(playerDTO);
            player = playerService.savePlayer(player);
            handleResponse(resp, SC_CREATED, objectMapper.writeValueAsString(player));
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.warn("invalid input provided for saving player: {}", playerDTO);
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerId = null;
        try {
            handleBadContentType(req, HEADER_VALUE_APPLICATION_JSON);
            playerId = extractPlayerId(req, PLAYERS_REQUEST_PATH);
            Objects.requireNonNull(playerId, "required playerId in path is null!");
            var playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
            validateAndHandleInvalid(playerDTO);
            var updatedPlayer = playerService.updatePlayerById(playerDTO, playerId);
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedPlayer));
        } catch (NullPointerException | JsonProcessingException | IllegalArgumentException e) {
            log.warn("invalid input provided for updating of player: {}!", playerId);
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerId = null;
        try {
            playerId = extractPlayerId(req, PLAYERS_REQUEST_PATH);
            Objects.requireNonNull(playerId, "required playerId in path is null!");
            if (playerService.deletePlayerById(playerId)) {
                handleResponse(resp, SC_OK, format("player: %s deleted successfully!", playerId));
            } else {
                handleResponse(resp, SC_BAD_REQUEST, format("player: %s could not be deleted successfully!", playerId));
            }
        } catch (NoResultException e){
            log.error("deleting of player: {} failed!", playerId);
            handleResponse(resp, SC_BAD_REQUEST, format("failed to delete player: %s", playerId));
        }
        catch (IllegalStateException | PersistenceException e) {
            log.error("deleting of player: {} failed!", playerId);
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, format("failed to delete player: %s", playerId));
        }
    }
}
