package org.richard.home.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.richard.home.web.dto.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Optional.ofNullable;

public class TeamServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamServlet.class);
    private TeamService teamService;
    private ObjectMapper objectMapper;

    private Validator validator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.validator = ApplicationConfiguration.VALIDATOR_FACTORY.getValidator();
        this.objectMapper = ApplicationConfiguration.OBJECT_MAPPER;
        this.teamService = StaticApplicationConfiguration.TEAM_SERVICE_INSTANCE;
        log.info("init method was called...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        log.info("doGet method was called...");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            resp.addHeader("Content-Type", "application/json");
            resp.addHeader("X-Powered-By", "Jetty 11");
            resp.getWriter().write("team successfully created. TeamId= " + createdTeam.getId());
        } catch (LeagueDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("Content-Type", "application/json");
            resp.addHeader("X-Powered-By", "Jetty 11");
            resp.getWriter().write("league specified does not exist! " + e.getMessage() + "\n");
        }


    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    public TeamService getTeamService() {
        return teamService;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    private TeamDto mapToTeamDto(HttpServletRequest request) throws JsonProcessingException {
        return new TeamDto(
                request.getParameter("name"),
                Integer.valueOf(ofNullable(request.getParameter("budget")).orElseGet(() -> "0")),
                request.getParameter("logoUrl"),
                request.getParameter("owner"),
                request.getParameter("tla"),
                null,
//                objectMapper.readValue(request.getParameter("address"), AddressDTO.class),
                request.getParameter("phone"),
                request.getParameter("website"),
                request.getParameter("email"),
                request.getParameter("venue"),
                Integer.valueOf(ofNullable(request.getParameter("wyId")).orElseGet(() -> "0")),
                request.getParameter("leagueId"));

    }
}
