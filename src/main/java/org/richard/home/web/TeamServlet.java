package org.richard.home.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.richard.home.config.ApplicationConfiguration;
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

import static java.util.Optional.ofNullable;
import static org.richard.home.web.WebConstants.HEADER_NAME_CONTENT_TYPE;

public class TeamServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamServlet.class);
    private TeamService teamService;
    private ObjectMapper objectMapper;

    private Validator validator;

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.validator = ApplicationConfiguration.VALIDATOR_FACTORY.getValidator();
        this.objectMapper = ApplicationConfiguration.OBJECT_MAPPER;
        this.teamService = StaticApplicationConfiguration.TEAM_SERVICE_INSTANCE;
        log.info("init method was called...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doGet method was called...");
        String teamId = null;
        try {
            teamId = Objects.requireNonNull(req.getParameter("id"));
            Team team = Optional.ofNullable(teamService.findTeamById(teamId)).orElseThrow(() -> new NoResultException());
            resp.setStatus(HttpServletResponse.SC_OK);
            addDefaultHeader(resp);
            resp.getWriter().println(objectMapper.writeValueAsString(team));
        } catch (NullPointerException e) {
            handleFailure(teamId, resp, "teamId was null");
        } catch (NoResultException e) {
            handleFailure(teamId, resp, "team with id: %s could not be found!");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("doPost method was called...");
        TeamDto teamDto = null;
        Team createdTeam = null;
        if (req.getHeader("Content-Type").equals("application/x-www-form-urlencoded")) {
            teamDto = mapToTeamDto(req);
        } else if (req.getHeader("Content-Type").equals("application/json")) {
            teamDto = objectMapper.readValue(req.getInputStream(), TeamDto.class);
        }
        if (this.validator.validate(teamDto).stream().iterator().hasNext()) {
            throw new IllegalStateException("validation error");
        }
        try {
            createdTeam = this.teamService.createTeam(teamDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            addDefaultHeader(resp);
            resp.getWriter().println(String.format("team successfully created. TeamId: %d", createdTeam.getId()));
        } catch (LeagueDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            addDefaultHeader(resp);
            resp.getWriter().println(String.format("league specified does not exist! %s", e.getMessage()));
        }


    }

    // /api/teams/id/66
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String teamId = null;
        TeamDto teamDto = null;
        try {
            if (req.getRequestURI().startsWith("/api/teams/id/")) {
                teamId = req.getRequestURI().split("/api/teams/id/")[1];
            }
            if (req.getHeader(HEADER_NAME_CONTENT_TYPE).equals("application/json")) {
                teamDto = objectMapper.readValue(req.getInputStream(), TeamDto.class);
            }
            var updatedTeam = teamService.updateTeam(teamId, teamDto);
            resp.setStatus(HttpServletResponse.SC_OK);
            addDefaultHeader(resp);
            resp.getWriter().println(objectMapper.writeValueAsString(updatedTeam));

        } catch (LeagueDoesNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String teamId = null;
        try {
            teamId = Objects.requireNonNull(req.getParameter("id"));
            if (teamService.deleteTeam(teamId)) {
                handleSuccessfulResponse(teamId, resp, "deleting of team: %s was successful!");
            } else {
                handleFailure(teamId, resp, "deleting of team: %s was NOT successful!");
            }
        } catch (NullPointerException e) {
            handleFailure(teamId, resp, "team with the provided id:  %s does not exist. \n");
        }

    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

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
        response.setStatus(HttpServletResponse.SC_OK);
        addDefaultHeader(response);
        response.getWriter().println(String.format(message, teamId));

    }

    private void handleFailure(String teamId, HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        addDefaultHeader(response);
        response.getWriter().println(String.format(message, teamId));
    }
}
