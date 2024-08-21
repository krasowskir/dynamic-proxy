package org.richard.home;


import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.richard.home.config.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionTest {

    private static final String HOST, USERNAME, PASSWORD, PORT, DATABASE_NAME;
    private static final Logger log = LoggerFactory.getLogger(ConnectionTest.class);
    private static final Properties PROPS;

    static {
        PROPS = new Properties();
        try {
            PROPS.load(
                    Files.newInputStream(
                            Path.of(
                                    DatabaseConfiguration.class.getClassLoader().getResource("application.properties").toURI())));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        HOST = PROPS.getProperty("database.db.host");
        PORT = PROPS.getProperty("database.db.port");
        USERNAME = PROPS.getProperty("database.db.username");
        PASSWORD = PROPS.getProperty("database.db.password");
        DATABASE_NAME = PROPS.getProperty("database.db.database_name");

    }

    @Test
    public void testAmountOfConnections() {
        int amountOfConnections = 0;
        int max = 5000;
        log.info("start...");
        try {
            Class.forName("org.postgresql.Driver");
            while (amountOfConnections < max) {
                Connection connection = DriverManager.getConnection(String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, DATABASE_NAME), USERNAME, PASSWORD);
                connection.isValid(2);
                //            log.info("connection established: number {}, established: {}", amountOfConnections, connection.isValid(2));
                amountOfConnections++;
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.info("{} connections established", amountOfConnections);
            log.error("error while obtaining connection");
        }
    }

    @Test
    public void testAmountOfConnectionsWithConnectionPool() {
        int amountOfConnections = 0;
        int max = 5000;
        log.info("start...");
        try {
            Class.forName("org.postgresql.Driver");
            String jdbcUrl = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setPoolName("cookbook");
            dataSource.setMaximumPoolSize(200);
            dataSource.setMinimumIdle(2);
            dataSource.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
            dataSource.addDataSourceProperty("prepStmtCacheSize", 512);
            dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 1024);
            dataSource.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
            while (amountOfConnections < max) {
                Connection connection = dataSource.getConnection();
                connection.isValid(2);
                amountOfConnections++;
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.info("{} connections established", amountOfConnections);
            log.error("error while obtaining connection");
        }
    }
}
