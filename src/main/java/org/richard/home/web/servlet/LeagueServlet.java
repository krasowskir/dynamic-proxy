package org.richard.home.web.servlet;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.League;
import org.richard.home.service.LeagueService;
import org.richard.home.service.dto.LeagueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.richard.home.config.StaticApplicationConfiguration.OBJECT_MAPPER;
import static org.richard.home.config.StaticApplicationConfiguration.VALIDATOR_FACTORY;
import static org.richard.home.web.WebConstants.*;

// ToDo: refactoring nötig. Siehe PlayerServlet.
//@WebServlet(urlPatterns = "/leagues")
public class LeagueServlet extends HttpServlet {

    private static final String LEAGUE_PATH = "/api/leagues";
    private static final Pattern LEAGUE_PATTERN = Pattern.compile(LEAGUE_PATH);
    private static final String PARAMETER_LEAGUE_NAME = "name";
    private static final String PARAMETER_LEAGUE_ID = "id";
    private static final String PARAMETER_LEAGUE_CODE = "code";
    private static Logger log = LoggerFactory.getLogger(LeagueServlet.class);
    private Pattern pathPattern = Pattern.compile("/api/leagues/.*");
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

    private static void handleInvalidPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!LEAGUE_PATTERN.matcher(request.getRequestURI()).matches()) {
            handleResponse(response, SC_BAD_REQUEST, format("path used: %s is not appropriate!", request.getRequestURI()));
        }
    }

    private static void handleInvalidContentType(HttpServletRequest request, HttpServletResponse response, String validContentType) throws IOException {
        if (!request.getContentType().equals(validContentType)) {
            handleResponse(response, SC_BAD_REQUEST, format("content type used: %s is not appropriate!", request.getContentType()));
        }
    }

    private static void validateAndHandleInvalid(LeagueDTO leagueDTO) {
        var errors = VALIDATOR_FACTORY.getValidator().validate(leagueDTO);
        if (!errors.isEmpty()) {
            var errorMessagesCombined = errors.stream()
                    .map(error -> {
                        log.error("validation error: {}", error.getMessage());
                        return error.getMessage();
                    }).collect(Collectors.joining(";\n"));
            throw new IllegalArgumentException(errorMessagesCombined);
        }
    }

    private static void handleInvalidAmountOfParameters(int amountOfRequestParams) {
        if (amountOfRequestParams > 1 || amountOfRequestParams == 0) {
            throw new IllegalArgumentException(format("request parameter size cannot be: %s", amountOfRequestParams));
        }
    }

    @Override
    public void init(ServletConfig config) {
        log.info("init method without args was called...");
//        this.leagueService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(LeagueService.class);
        this.leagueService = StaticApplicationConfiguration.LEAGUE_SERVICE;
        this.objectMapper = OBJECT_MAPPER;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("received GET request on uri: {}", req.getRequestURI());
        var amountOfRequestParams = req.getParameterMap().size();
        try {
            handleInvalidContentType(req, resp, HEADER_VALUE_FORM_URL_ENCODED);
            handleInvalidAmountOfParameters(amountOfRequestParams);
            League foundLeague = switch (req.getParameterNames().nextElement()) {
                case PARAMETER_LEAGUE_ID -> leagueService.getLeague(req.getParameterValues(PARAMETER_LEAGUE_ID)[0]);
                case PARAMETER_LEAGUE_NAME ->
                        leagueService.getLeagueByName(req.getParameterValues(PARAMETER_LEAGUE_NAME)[0]);
                case PARAMETER_LEAGUE_CODE ->
                        leagueService.getLeagueByCode(req.getParameterValues(PARAMETER_LEAGUE_CODE)[0]);
                default -> null;
            };
            requireNonNull(foundLeague, format("no league found by provided request parameter: %s, value: %s",
                    req.getParameterNames().nextElement(), req.getParameterMap().get(req.getParameterNames().nextElement())[0]));
//            handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(foundLeague));
            handleResponse(resp, SC_OK, objectMapper.writeValueAsString(foundLeague));
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
        handleInvalidContentType(req, resp, HEADER_VALUE_APPLICATION_JSON);
        try {
//            LeagueDTO leagueDTO = StaticApplicationConfiguration.OBJECT_MAPPER.readValue(req.getInputStream(), LeagueDTO.class);
            LeagueDTO leagueDTO = objectMapper.readValue(req.getInputStream(), LeagueDTO.class);
            validateAndHandleInvalid(leagueDTO);
            var createdLeague = leagueService.createLeague(leagueDTO);
//            handleResponse(resp, SC_CREATED, OBJECT_MAPPER.writeValueAsString(createdLeague));
            handleResponse(resp, SC_CREATED, objectMapper.writeValueAsString(createdLeague));
        } catch (IllegalArgumentException | NullPointerException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException | PersistenceException e) {
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleInvalidContentType(req, resp, HEADER_NAME_CONTENT_TYPE);
        try {
            if (pathPattern.matcher(req.getRequestURI()).matches()
                    && req.getContentType().equals(HEADER_VALUE_APPLICATION_JSON)) {
                String leagueId = req.getRequestURI().split(LEAGUE_PATH)[1].startsWith("/") ?
                        req.getRequestURI().split(LEAGUE_PATH)[1].substring(1) :
                        req.getRequestURI().split(LEAGUE_PATH)[1];
//                LeagueDTO leagueDTO = OBJECT_MAPPER.readValue(req.getInputStream(), LeagueDTO.class);
                LeagueDTO leagueDTO = objectMapper.readValue(req.getInputStream(), LeagueDTO.class);
                validateAndHandleInvalid(leagueDTO);
                var updatedLeague = leagueService.updateLeague(leagueId, leagueDTO);
//                handleResponse(resp, SC_OK, OBJECT_MAPPER.writeValueAsString(updatedLeague));
                handleResponse(resp, SC_OK, objectMapper.writeValueAsString(updatedLeague));
            }
        } catch (IllegalArgumentException | DatabindException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException | PersistenceException e) {
            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            handleInvalidPath(req, resp);
            String leagueId = req.getRequestURI().split(LEAGUE_PATH)[1].startsWith("/") ?
                    req.getRequestURI().split(LEAGUE_PATH)[1].substring(1) :
                    req.getRequestURI().split(LEAGUE_PATH)[1];
            boolean deletionSucceeded = leagueService.deleteLeague(leagueId);
            if (deletionSucceeded) {
                handleResponse(resp, SC_OK, format("deletion of league: %s was successful!", leagueId));
            } else {
                throw new IllegalStateException(format("deletion of league %s failed!", leagueId));
            }

        } catch (NullPointerException | IllegalStateException e) {
            handleResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }
}
