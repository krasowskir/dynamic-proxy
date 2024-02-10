package org.richard.home.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.infrastructure.PlayerService;
import org.richard.home.infrastructure.VerifyAge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Optional;

//@WebServlet(name = "playerServlet", urlPatterns = {"/player/*"}, loadOnStartup = 1)
public class PlayerServlet extends HttpServlet {

    private VerifyAge playerService;

    private static Logger log = LoggerFactory.getLogger(PlayerServlet.class);

    public PlayerServlet() {
    }

    public void setPlayerService(VerifyAge playerService) {
        this.playerService = playerService;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        log.info("service method was called...");
        super.service(req, res);
    }

    @Override
    public void destroy() {
        log.info("destroy method was called...");
        super.destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("init method was called...");
        Optional.ofNullable(config.getInitParameterNames())
                .orElseGet(() -> {
                    log.info("config did not contain init parameter!");
                    return null;
                })
                .asIterator()
                .forEachRemaining(elem -> log.info("param name: {} \n", elem));
        super.init(config);
    }

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("real path of file in servlet context: {}", req.getServletContext().getRealPath("rich-file"));
        this.playerService = RequestContextUtils.findWebApplicationContext(req).getBean(PlayerService.class);
        playerService.verifyAge(Integer.parseInt(req.getParameter("age1")), Integer.parseInt(req.getParameter("age2")));
        resp.getWriter().write("PlayerServlet works!");
    }
}
