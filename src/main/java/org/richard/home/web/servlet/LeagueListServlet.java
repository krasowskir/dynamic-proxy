package org.richard.home.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.League;
import org.richard.home.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.richard.home.config.StaticApplicationConfiguration.OBJECT_MAPPER;

public class LeagueListServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LeagueListServlet.class);
    private LeagueService leagueService;
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

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");
        this.leagueService = StaticApplicationConfiguration.LEAGUE_SERVICE;
        this.objectMapper = OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
//            abortDirectCalls(req, resp);
            var leagueList = leagueService.listLeagues();
            req.getServletContext().setAttribute("forward", null);
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(leagueList));
        } catch (IllegalArgumentException | NullPointerException | NoResultException e) {
            log.error(e.getMessage());
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }

    }

    // jeder thread kann forward setzen und es würde ok sein. Muss auch den Aufruferthread prüfen -> ThreadLocal!
    private static void abortDirectCalls(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getServletContext().getAttribute("forward") == null ||
                !Boolean.parseBoolean(String.valueOf(req.getServletContext().getAttribute("forward")))){
            handleResponse(resp, SC_BAD_REQUEST, "not allowed to access the leagues from the root path! use /api/leagues/list instead!");
        }
    }
}
