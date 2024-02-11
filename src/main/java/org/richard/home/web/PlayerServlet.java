package org.richard.home.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.richard.home.infrastructure.VerifyAge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;

public class PlayerServlet extends HttpServlet {

    private VerifyAge playerService;

    private static Logger log = LoggerFactory.getLogger(PlayerServlet.class);

    public PlayerServlet() {
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
        super.init(config);
    }

    @Override
    public void init() throws ServletException {
        log.info("init method without args was called...");
        this.playerService = ContextLoaderListener.getCurrentWebApplicationContext().getBean(VerifyAge.class);
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("real path of file in servlet context: {}", req.getServletContext().getRealPath("rich-file"));
        playerService.verifyAge(Integer.parseInt(req.getParameter("age1")), Integer.parseInt(req.getParameter("age2")));
        resp.getWriter().write("PlayerServlet works!");
    }
}
