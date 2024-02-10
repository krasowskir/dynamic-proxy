package org.richard.home;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.ServletsConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.richard.home.config.GeneralConfiguration;
import org.richard.home.infrastructure.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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

        WebAppContext context = new WebAppContext();
        context.addConfiguration(new ServletsConfiguration());

//        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
//        appContext.register(GeneralConfiguration.class);
//        appContext.setServletContext(context.getServletContext());
//        appContext.refresh();
//        appContext.start();

//        context.configure();
        context.setWar("target/dynamic-proxy-1.0.war");
        context.setContextPath("/api");

        server.setHandler(context);
        server.start();
    }

}
