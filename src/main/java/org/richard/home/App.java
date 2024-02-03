package org.richard.home;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setWar("target/dynamic-proxy-1.0.war");
        context.setContextPath("/api");

        server.setHandler(context);
        server.start();
    }

}
