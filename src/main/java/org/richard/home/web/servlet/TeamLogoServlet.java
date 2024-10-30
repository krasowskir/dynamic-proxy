package org.richard.home.web.servlet;

import jakarta.persistence.NoResultException;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.richard.home.config.StaticApplicationConfiguration.TEAM_SERVICE_INSTANCE;
import static org.richard.home.web.WebUtils.addDefaultHeader;

public class TeamLogoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TeamLogoServlet.class);

    public static final String SERVLET_NAME = "teamLogoServlet";

    private TeamService teamService;

    @Override
    public void init() throws ServletException {
        this.teamService = TEAM_SERVICE_INSTANCE;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("TeamLogoServlet GET request on Thread: {}", Thread.currentThread().getId());
        var teamId = extractAndCleanup(req, "teamId");
        try {
            AsyncContext asyncContext = req.startAsync(req, resp);
            asyncContext.start(() -> {
                try {
                    byte[] teamLogo = teamService.getTeamLogoAsync(teamId);
                    log.info("file obtained: {} byte", teamLogo.length);

                    resp.setStatus(SC_OK);
                    resp.setContentLength(teamLogo.length);
                    resp.setHeader("Content-Type", "application/octet-stream");
//                    resp.setHeader("Content-Disposition", "attachment; filename=logo");
                    try (var in = new ByteArrayInputStream(teamLogo)) {
                        try (var out = asyncContext.getResponse().getOutputStream()) {
                            in.transferTo(out);
                        }
                    } catch (IOException e) {
                        log.error("could not write binary file to servlet output", e);
                        throw new RuntimeException(e);
                    }
                } catch (NoResultException e) {
                    log.warn("team logo could not be found. {}", e.getMessage());
                    handleResponse(resp, SC_BAD_REQUEST, "team logo could not be found! %s".formatted(e.getMessage()));
                } catch (RuntimeException e) {
                    log.warn("could not update team logo. General issue: {}", e.getMessage());
                    handleResponse(resp, SC_SERVICE_UNAVAILABLE, "could not update team logo. General issue: %s".formatted(e.getMessage()));
                } finally {
                    asyncContext.complete();
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        log.info("TeamLogoServlet PUT request on Thread: {}", Thread.currentThread().getId());
        try {
            String teamId = extractAndCleanup(req, "teamId");
            log.info("teamId passed to doPut method: {}", teamId);
            AsyncContext asyncContext = req.startAsync(req, resp);
            asyncContext.start(() -> {
                        try {
                            log.info("request async started. Time: {} Thread: {}", Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis())), Thread.currentThread().getId());
                            HttpRequest httpRequest = HttpRequest.newBuilder(
                                            URI.create("http://localhost:8080/api/documents/binary"))
                                    .POST(HttpRequest.BodyPublishers.ofByteArray(req.getInputStream().readAllBytes()))
                                    .header("Content-Type", "application/octet-stream")
                                    .header("Filename", Objects.requireNonNull(req.getHeader("Filename")))
                                    .build();
                            HttpClient httpClient = HttpClient.newHttpClient();
                            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                            String logoObjectId = response.body().substring("objectId: ".length());
                            log.info("objectId: {}", logoObjectId);
                            var result = this.teamService.updateTeamLogo(teamId, logoObjectId);
                            log.info("request async completed.Time: {}, Thread: {}", Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis())), Thread.currentThread().getId());
                            if (result) {
                                handleResponse(resp, SC_OK, "successfully updated the logo!");
                            } else {
                                handleResponse(resp, SC_INTERNAL_SERVER_ERROR, "something went wrong");
                            }
                            req.getAsyncContext().complete();
                        } catch (ClosedChannelException | ConnectException | InterruptedException e) {
                            log.error("remote service document-management seems to be down. {}", e.getMessage());
                            handleResponse(resp, SC_INTERNAL_SERVER_ERROR, "remote service document-management seems to be down.");
                            req.getAsyncContext().complete();
                        } catch (IOException e) {
                            log.error("IOException. {}", e.getMessage());
                            req.getAsyncContext().complete();
                        }
                    }
            );
        } catch (NoResultException e) {
            log.warn("team logo could not be updated. {}", e.getMessage());
            handleResponse(resp, SC_BAD_REQUEST, "team logo could not be updated! %s".formatted(e.getMessage()));
        } catch (RuntimeException e) {
            log.warn("could not update team logo. General issue: {}", e.getMessage());
            handleResponse(resp, SC_SERVICE_UNAVAILABLE, "could not update team logo. General issue: %s".formatted(e.getMessage()));
        }

    }

    private static String extractAndCleanup(HttpServletRequest req, String parameter) {
        return Optional.of((String) req.getServletContext().getAttribute(parameter))
                .stream()
                .map(String::trim)
                .peek(param -> req.getServletContext().removeAttribute(param))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("parameter: %s cannot be null!".formatted(parameter)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    public static void handleResponse(HttpServletResponse resp, int scBadRequest, String e) {
        resp.setStatus(scBadRequest);
        addDefaultHeader(resp);
        try {
            resp.getWriter().println(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
