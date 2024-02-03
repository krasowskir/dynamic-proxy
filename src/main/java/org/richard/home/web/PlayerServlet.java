package org.richard.home.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class PlayerServlet extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(PlayerServlet.class);
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("PlayerServlet works!");
    }
}
