package org.richard.home.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.persistence.RollbackException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.service.TeamService;
import org.richard.home.service.dto.TeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.lang.String.format;
import static org.richard.home.config.StaticApplicationConfiguration.OBJECT_MAPPER;
import static org.richard.home.config.StaticApplicationConfiguration.TEAM_SERVICE_INSTANCE;
import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;
import static org.richard.home.web.WebConstants.HEADER_VALUE_FORM_URL_ENCODED;
import static org.richard.home.web.WebUtils.*;

public class TeamServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamServlet.class);
    private static final String TEAMS_PATH = "/api/teams";
    private TeamService teamService;
    private ObjectMapper objectMapper;

    private static String extractTeamId(HttpServletRequest req, String fromPath) {
        return Pattern.compile(fromPath.concat(".+")).matcher(req.getRequestURI()).matches() ?
                Optional.of(req.getRequestURI().split(fromPath)[1].substring(0))
                        .stream()
                        .takeWhile(elem -> !elem.isBlank())
                        .findFirst()
                        .orElse(null) : null;
    }

    @Override
    public void init(ServletConfig config) {
        this.objectMapper = OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.teamService = TEAM_SERVICE_INSTANCE;
        log.info("init method was called...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doGet method was called...");
        try {
            final Integer teamId = Integer.valueOf(
                    Objects.requireNonNull(
                            req.getParameter("id"), "request parameter id was null!"));
            Team team = Optional.ofNullable(teamService.findTeamById(teamId.toString()))
                    .orElseThrow(() -> new NoResultException(format("team with id: %s could not be found!", teamId)));
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(team));
        } catch (NullPointerException | NumberFormatException e) {
            log.warn(e.getMessage());
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (NoResultException e) {
            log.warn(e.getMessage());
            handleResponse(resp, SC_NOT_FOUND, e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doPost method was called...");
        try {
            TeamDTO teamDto = mapTeamDTO(req);
            validateAndHandleInvalid(teamDto);
            var createdTeam = this.teamService.createTeam(teamDto);
            log.info("team successfully created. TeamId: {}", createdTeam.getId());
            handleResponse(resp, SC_CREATED, objectMapper.writeValueAsString(createdTeam));
        } catch (LeagueDoesNotExistException e) {
            handleResponse(resp, SC_BAD_REQUEST, format("league specified does not exist! %s", e.getMessage()));
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.warn("invalid input provided!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            handleBadContentType(req, HEADER_VALUE_APPLICATION_JSON);
            String teamId = Objects.requireNonNull(
                    extractTeamId(req, TEAMS_PATH + "/id/"), "teamId was null");
            TeamDTO teamDto = objectMapper.treeToValue(objectMapper.readTree(req.getInputStream()), TeamDTO.class);
            validateAndHandleInvalid(teamDto);
            var updatedTeam = teamService.updateTeam(teamId, teamDto);
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedTeam));
        } catch (LeagueDoesNotExistException e) {
            log.error("request contained leagueId which could not be found!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn(e.getMessage());
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (RollbackException e) {
            log.warn("could not update the team successfully!");
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String teamId = Objects.requireNonNull(req.getParameter("id"),
                    "request parameter id: %s cannot be null!");
            if (teamService.deleteTeam(teamId)) {
                handleResponse(resp, SC_OK, String.format("deleting of team: %s was successful!", teamId));
            } else {
                handleResponse(resp, SC_BAD_REQUEST, String.format("deleting of team: %s was NOT successful!"));
            }
        } catch (NullPointerException e) {
            log.error("request to delete team with id: {} failed!", req.getParameter("id"));
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }


    private TeamDTO mapTeamDTO(HttpServletRequest req) throws IOException {
        switch (stripCharset(req.getContentType())) {
            case HEADER_VALUE_FORM_URL_ENCODED -> {
                return null;
            }
            case HEADER_VALUE_APPLICATION_JSON -> {
                try (var input = req.getInputStream()) {
                    JsonNode jsonTree = objectMapper.readTree(input);
                    return objectMapper.treeToValue(jsonTree, TeamDTO.class);
                } catch (IllegalArgumentException | JsonProcessingException e) {
                    log.warn("could not read and parse team json!");
                    throw e;
                }
            }
            default -> {
                return null;
            }
        }
    }
}
