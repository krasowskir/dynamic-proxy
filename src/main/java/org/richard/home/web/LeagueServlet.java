package org.richard.home.web;

import com.fasterxml.jackson.databind.DatabindException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.League;
import org.richard.home.service.LeagueService;
import org.richard.home.web.dto.LeagueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.richard.home.config.StaticApplicationConfiguration.OBJECT_MAPPER;
import static org.richard.home.config.StaticApplicationConfiguration.VALIDATOR_FACTORY;
import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;
import static org.richard.home.web.WebConstants.HEADER_VALUE_FORM_URL_ENCODED;

public class LeagueServlet extends HttpServlet {

    private static final String LEAGUE_PATH = "/api/leagues";
    private static final String PARAMETER_LEAGUE_NAME = "name";
    private static final String PARAMETER_LEAGUE_ID = "id";
    private static final String PARAMETER_LEAGUE_CODE = "code";
    private static Logger log = LoggerFactory.getLogger(LeagueServlet.class);
    private Pattern pathPattern = Pattern.compile("/api/leagues/.*");
    private LeagueService leagueService;

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    private static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) throws IOException {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        resp.getWriter().println(e);
    }

    private static boolean requestWithContentType(HttpServletRequest req, String contentType) {
        return req.getContentType().equals(contentType);
    }

    private static void validateAndHandleInvalid(LeagueDTO leagueDTO) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(leagueDTO);
        if (!errors.isEmpty()){
            var errorMessagesCombined = errors.stream()
                    .map(error -> {
                        log.error("validation error: {}", error.getMessage());
                        return error.getMessage();
                    }).collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("received GET request on uri: {}", req.getRequestURI());
        var amountOfRequestParams = req.getParameterMap().size();
        try {
            if (requestWithContentType(req, HEADER_VALUE_FORM_URL_ENCODED)) {
                if (amountOfRequestParams > 1 || amountOfRequestParams == 0) {
                    throw new IllegalArgumentException(format("request parameter size cannot be: %s", req.getParameterMap().size()));
                } else {

                    League foundLeague = switch (req.getParameterNames().nextElement()) {
                        case PARAMETER_LEAGUE_ID ->
                                leagueService.getLeague(req.getParameterValues(PARAMETER_LEAGUE_ID)[0]);
                        case PARAMETER_LEAGUE_NAME ->
                                leagueService.getLeagueByName(req.getParameterValues(PARAMETER_LEAGUE_NAME)[0]);
                        case PARAMETER_LEAGUE_CODE ->
                                leagueService.getLeagueByCode(req.getParameterValues(PARAMETER_LEAGUE_CODE)[0]);
                        default -> null;
                    };
                    requireNonNull(foundLeague, format("no league found by provided request parameter: %s, value: %s",
                            req.getParameterNames().nextElement(), req.getParameterMap().get(req.getParameterNames().nextElement())[0]));
                    handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(foundLeague));
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("GET request received with not allowed amount {} of request parameters!", amountOfRequestParams);
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (req.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {
                LeagueDTO leagueDTO = StaticApplicationConfiguration.OBJECT_MAPPER.readValue(req.getInputStream(), LeagueDTO.class);
                String errorMessage = VALIDATOR_FACTORY.getValidator().validate(leagueDTO).stream()
                        .filter(Objects::nonNull)
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(","));

                if (!errorMessage.isBlank()) {
                    log.error("league DTO in request failed validation!");
                    throw new IllegalArgumentException(errorMessage);
                }

                var createdLeague = leagueService.createLeague(leagueDTO);
                handleResponse(resp, SC_CREATED, OBJECT_MAPPER.writeValueAsString(createdLeague));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (pathPattern.matcher(req.getRequestURI()).matches() && req.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {
                String leagueId = req.getRequestURI().split(LEAGUE_PATH)[1].startsWith("/") ?
                        req.getRequestURI().split(LEAGUE_PATH)[1].substring(1) :
                        req.getRequestURI().split(LEAGUE_PATH)[1];
                LeagueDTO leagueDTO = OBJECT_MAPPER.readValue(req.getInputStream(), LeagueDTO.class);
                validateAndHandleInvalid(leagueDTO);
                var updatedLeague = leagueService.updateLeague(leagueId, leagueDTO);
                handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(updatedLeague));
            }
        } catch (IllegalArgumentException | DatabindException e){
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (pathPattern.matcher(req.getRequestURI()).matches()) {
                String leagueId = req.getRequestURI().split(LEAGUE_PATH)[1].startsWith("/") ?
                        req.getRequestURI().split(LEAGUE_PATH)[1].substring(1) :
                        req.getRequestURI().split(LEAGUE_PATH)[1];
                boolean deletionSucceeded = leagueService.deleteLeague(leagueId);
                if (deletionSucceeded) {
                    handleResponse(resp, SC_OK, format("deletion of league: %s was successful!", leagueId));
                } else {
                    throw new IllegalStateException(format("deletion of league %s failed!", leagueId));
                }
            }
        } catch (NullPointerException | IllegalStateException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public void init(ServletConfig config) {
        log.info("init method without args was called...");
        this.leagueService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(LeagueService.class);
    }
}
