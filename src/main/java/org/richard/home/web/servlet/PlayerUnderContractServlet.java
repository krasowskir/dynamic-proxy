package org.richard.home.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.persistence.RollbackException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.Player;
import org.richard.home.infrastructure.exception.InternalServerError;
import org.richard.home.service.PlayerService;
import org.richard.home.service.TeamService;
import org.richard.home.service.dto.PlayersTeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.richard.home.web.WebUtils.handleResponse;

public class PlayerUnderContractServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PlayerUnderContractServlet.class);
    private PlayerService playerService;

    private TeamService teamService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;
        objectMapper.registerModule(new JavaTimeModule());
        this.playerService = StaticApplicationConfiguration.PLAYER_SERVICE_INSTANCE;
        this.teamService = StaticApplicationConfiguration.TEAM_SERVICE_INSTANCE;
    }

    // /api/players/{id}/contracts
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerId = null;
        try {
            playerId = String.valueOf(Objects.requireNonNull(req.getServletContext().getAttribute("playerId")));
            req.getServletContext().setAttribute("playerId", null);
            Optional.ofNullable(teamService.getCurrentTeamOfPlayer(playerId))
                    .ifPresent(elem -> {
                        try {
                            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(elem));
                        } catch (IOException e) {
                            log.error("something went wrong!");
                            throw new InternalServerError();
                        }
                    });
        } catch (NullPointerException e) {
            log.warn("playerId was null or contained only whitespaces!");
            handleResponse(resp, SC_BAD_REQUEST, "playerId was null or contained only whitespaces");
        } catch (NoResultException e) {
            log.warn("player with playerId: {} could not be matched with any team!", playerId);
            handleResponse(resp, SC_NOT_FOUND, String.format("player with playerId: %s could not be matched with any team!", playerId));
        } catch (InternalServerError e) {
            log.warn("super bad ");
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    //ToDo: clear servletContext after processing the request!

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerId = null;
        try {
            playerId = String.valueOf(Objects.requireNonNull(req.getServletContext().getAttribute("playerId")));
            req.getServletContext().setAttribute("playerId", null);
            var playersNewTeam = objectMapper.readValue(req.getInputStream(), PlayersTeamDTO.class);

            Player playerWithNewTeam = playerService.updateTeamOfPlayer(playerId, playersNewTeam.getTeamId());
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(playerWithNewTeam));
        } catch (NullPointerException e) {
            log.warn("playerId was null!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (RollbackException | IllegalStateException e) {
            log.error("some issue happened while updating the player: {}", playerId);
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String playerId = null;
        try {
            playerId = String.valueOf(Objects.requireNonNull(req.getServletContext().getAttribute("playerId")));
            var teamId = req.getPathInfo().replaceAll("/", "").trim();
            req.getServletContext().removeAttribute("playerId");
            playerService.deletePlayersContract(playerId, teamId);
            handleResponse(resp, SC_OK, "Deletion of players contract was successful!");
        } catch (NullPointerException e) {
            log.warn("playerId was null!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (RollbackException | IllegalStateException e) {
            log.error("some issue happened while updating the player: {}", playerId);
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("player contract could not be deleted!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }
}
