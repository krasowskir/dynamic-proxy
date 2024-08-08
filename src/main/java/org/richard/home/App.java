package org.richard.home;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.ServletsConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.richard.home.config.GeneralConfiguration;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.web.HealthServlet;
import org.richard.home.web.LeagueServlet;
import org.richard.home.web.PlayerServlet;
import org.richard.home.web.TeamServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        App app = new App();
        try {

            log.info("server start...");
            app.startServer();
            log.info("server started.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Servlet web application context
        WebAppContext servletContext = new WebAppContext();
        servletContext.addConfiguration(new ServletsConfiguration());

        // Spring web application context
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(GeneralConfiguration.class);
        applicationContext.register(StaticApplicationConfiguration.class);
//        var staticConfig = new StaticApplicationConfiguration();
        applicationContext.setServletContext(servletContext.getServletContext());
        applicationContext.refresh();

        // initialize start Spring context
        servletContext.addConfiguration();
        servletContext.addEventListener(new ContextLoaderListener(applicationContext));

        // ToDo: Finde heraus, wie man hier PlayerService injezieren kann!!!
        servletContext.addServlet(PlayerServlet.class, "/players/*");
        servletContext.addServlet(TeamServlet.class, "/teams/*");
        servletContext.addServlet(LeagueServlet.class, "/leagues/*");
        servletContext.addServlet(HealthServlet.class, "/health/*");
//        context.addFilter(ArgumentsValidatingFilter.class, "/player/*", EnumSet.of(DispatcherType.REQUEST));
//        servletContext.setErrorHandler();
//        servletContext.setWar("target/dynamic-proxy-1.0.war");
        servletContext.setResourceBase("target/dynamic-proxy-1.0.jar");
        servletContext.setContextPath("/api");

        server.setHandler(servletContext);
        boolean isStarted;
        do {
            server.start();
            Thread.sleep(1000);
            isStarted = server.isStarted();
            log.info("waiting for server to complete starting... isStarted: {}", isStarted);
        } while (!server.isStarted());

    }

}
