package org.richard.home.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.service.TeamService;
import org.richard.home.web.dto.AddressDTO;
import org.richard.home.web.dto.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.richard.home.config.StaticApplicationConfiguration.*;
import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;
import static org.richard.home.web.WebConstants.HEADER_VALUE_FORM_URL_ENCODED;

//@WebServlet(urlPatterns = "/teeams")
public class TeamServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamServlet.class);
    private static final String TEAMS_PATH = "/api/teams";
    private static Pattern TEAM_PATTERN = Pattern.compile(TEAMS_PATH + "/id/");
    private TeamService teamService;
    private ObjectMapper objectMapper;

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    private static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    private static void validateAndHandleInvalid(TeamDto teamDto) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(teamDto);
        if (!errors.isEmpty()) {
            var errorMessagesCombined = errors.stream()
                    .map(error -> {
                        log.error("validation error: {}", error.getMessage());
                        return error.getMessage();
                    }).collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    private static void handleInvalidPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!TEAM_PATTERN.matcher(request.getRequestURI()).matches()) {
            handleResponse(response, SC_BAD_REQUEST, format("path used: %s is not appropriate!", request.getRequestURI()));
        }
    }

    private static void handleInvalidContentType(HttpServletRequest request, HttpServletResponse response, String validContentType) throws IOException {
        if (!request.getContentType().equals(validContentType)) {
            handleResponse(response, SC_BAD_REQUEST, format("content type used: %s is not appropriate!", request.getContentType()));
        }
    }

    private static String extractTeamId(HttpServletRequest req, String fromPath) {
        return req.getRequestURI().split(fromPath)[1];
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.objectMapper = OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.teamService = TEAM_SERVICE_INSTANCE;
        log.info("init method was called...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doGet method was called...");
        try {
            final String teamId = Objects.requireNonNull(req.getParameter("id"), "request parameter id was null!");
            Team team = Optional.ofNullable(teamService.findTeamById(teamId))
                    .orElseThrow(() -> new NoResultException(format("team with id: %s could not be found!", teamId)));
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(team));
        } catch (NullPointerException | NoResultException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doPost method was called...");
        try {
            TeamDto teamDto = mapTeamDTO(req);
            validateAndHandleInvalid(teamDto);
            var createdTeam = this.teamService.createTeam(teamDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            addDefaultHeader(resp);
            resp.getWriter().println(format("team successfully created. TeamId: %d", createdTeam.getId()));
        } catch (LeagueDoesNotExistException e) {
            handleResponse(resp, SC_BAD_REQUEST, format("league specified does not exist! %s", e.getMessage()));
        } catch (IllegalArgumentException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }


    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            handleInvalidPath(req, resp);
            handleInvalidContentType(req, resp, HEADER_VALUE_APPLICATION_JSON);
            String teamId = extractTeamId(req, TEAMS_PATH + "/id/");
            TeamDto teamDto = objectMapper.readValue(req.getInputStream(), TeamDto.class);
            validateAndHandleInvalid(teamDto);
            var updatedTeam = teamService.updateTeam(teamId, teamDto);
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedTeam));
        } catch (LeagueDoesNotExistException e) {
            log.error("request contained leagueId which could not be found!");
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String teamId = Objects.requireNonNull(req.getParameter("id"),
                    "request parameter id: %s cannot be null!");
            if (teamService.deleteTeam(teamId)) {
                handleSuccessfulResponse(teamId, resp, "deleting of team: %s was successful!");
            } else {
                handleFailure(teamId, resp, "deleting of team: %s was NOT successful!");
            }
        } catch (NullPointerException e) {
            log.error("request to delete team with id: {} failed!", req.getParameter("id"));
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    // ToDo: handle Address and AddressDTO properly!
    private TeamDto mapToTeamDto(HttpServletRequest request) {
        return new TeamDto(
                request.getParameter("name"),
                Integer.valueOf(ofNullable(request.getParameter("budget")).orElseGet(() -> "0")),
                request.getParameter("logoUrl"),
                request.getParameter("owner"),
                request.getParameter("tla"),
                (AddressDTO) null,
//                objectMapper.readValue(request.getParameter("address"), AddressDTO.class),
                request.getParameter("phone"),
                request.getParameter("website"),
                request.getParameter("email"),
                request.getParameter("venue"),
                Integer.valueOf(ofNullable(request.getParameter("wyId")).orElseGet(() -> "0")),
                request.getParameter("leagueId"));

    }

    private void handleSuccessfulResponse(String teamId, HttpServletResponse response, String message) throws IOException {
        response.setStatus(SC_OK);
        addDefaultHeader(response);
        response.getWriter().println(format(message, teamId));

    }

    private void handleFailure(String teamId, HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        addDefaultHeader(response);
        response.getWriter().println(format(message, teamId));
    }

    private TeamDto mapTeamDTO(HttpServletRequest req) throws IOException {
        TeamDto teamDto = null;
        switch (req.getContentType()) {
            case HEADER_VALUE_FORM_URL_ENCODED -> teamDto = mapToTeamDto(req);
            case HEADER_VALUE_APPLICATION_JSON -> teamDto = objectMapper.readValue(req.getInputStream(), TeamDto.class);
        }
        return teamDto;
    }
}
