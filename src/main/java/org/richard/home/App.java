package org.richard.home;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.ServletsConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.richard.home.config.ApplicationConfiguration;
import org.richard.home.config.GeneralConfiguration;
import org.richard.home.web.PlayerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        App app = new App();
        try {
            log.info("server started...");
            app.startServer();
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
        WebAppContext context = new WebAppContext();
        context.addConfiguration(new ServletsConfiguration());

        // Spring web application context
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(GeneralConfiguration.class);
        appContext.register(ApplicationConfiguration.class);
        appContext.setServletContext(context.getServletContext());
        appContext.refresh();

        // initialize start Spring context
        context.addEventListener(new ContextLoaderListener(appContext));
        context.addServlet(PlayerServlet.class, "/player/*");
        context.setWar("target/dynamic-proxy-1.0.war");
        context.setContextPath("/api");

        server.setHandler(context);
        server.start();
    }

}
