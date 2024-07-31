package org.richard.home.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.service.PlayerService;
import org.richard.home.web.dto.PlayerDTO;
import org.richard.home.web.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.richard.home.web.WebConstants.HEADER_NAME_CONTENT_TYPE;

public class PlayerServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PlayerServlet.class);
    private PlayerService playerService;
    private PlayerMapper playerMapper;
    private ObjectMapper objectMapper;
    private Validator validator;

    public PlayerServlet() {
    }

    private static void addDefaultHeader(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("X-Powered-By", "Jetty 11");
    }

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");

        this.playerService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(PlayerService.class);
        this.playerMapper = StaticApplicationConfiguration.PLAYER_MAPPER_INSTANCE;
        this.objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;
        this.validator = StaticApplicationConfiguration.VALIDATOR_FACTORY.getValidator();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("real path of file in servlet context: {}", req.getServletContext().getRealPath("rich-file"));
        log.info("aktueller thread: {}", Thread.currentThread().getId());
        switch (req.getParameterNames().nextElement()) {
            case "age" -> {
                var players = this.playerService.findPlayerByAge(Integer.parseInt(req.getParameter("age")));
                players.forEach(player -> {
                    try {
                        resp.getWriter().write(String.format("Player found: %s \n", player.toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            case "name" -> {
                var player = this.playerService.findPlayer(req.getParameter("name"));
                resp.getWriter().write(String.format("Player found: %s", player.toString()));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PlayerDTO playerDTO = null;
        if (req.getHeader(HEADER_NAME_CONTENT_TYPE).equals("application/json")) {
            try {

                playerDTO = objectMapper.readValue(req.getInputStream(), PlayerDTO.class);
                String errorMessage = validator.validate(playerDTO).stream()
                        .filter(Objects::nonNull)
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(","));
                if (!errorMessage.isBlank()) {
                    log.error("player DTO in request failed validation!");
                    throw new IllegalArgumentException(errorMessage);
                }
                var player = PlayerMapper.fromWebLayerTo(playerDTO);
                player = playerService.savePlayer(player);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                addDefaultHeader(resp);
                resp.getWriter().println(objectMapper.writeValueAsString(player));
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                addDefaultHeader(resp);
                resp.getWriter().println(e.getMessage());
            }
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

    private void handleSuccessfulResponse(String teamId, HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        addDefaultHeader(response);
        response.getWriter().println(String.format(message, teamId));
    }
}
