package org.richard.home;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.ServletsConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.richard.home.config.StaticApplicationConfiguration;
import org.richard.home.domain.Address;
import org.richard.home.domain.Country;
import org.richard.home.infrastructure.exception.InternalServerError;
import org.richard.home.web.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        App app = new App();
        try {

            log.info("server start...");
            app.startServer();
            log.info("server started.");
            if (Arrays.asList(args).contains("address")) {
                app.populateAddresses();
            }
            if (Arrays.asList(args).contains("teamAddress")) {
                app.populateTeamAddresses("extracted_addresses.txt");
            }
        } catch (InternalServerError e) {
            log.error(e.getMessage());
        }
    }

    public void startServer() {
        Server server = null;
        try {
            server = new Server();
            MBeanContainer mBeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(8080);
            server.addConnector(connector);
            server.addBean(mBeanContainer);

            // Servlet web application context
            WebAppContext servletContext = new WebAppContext();
            servletContext.addConfiguration(new ServletsConfiguration());

            // Spring web application context
            AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
            applicationContext.register(StaticApplicationConfiguration.class);
            applicationContext.setServletContext(servletContext.getServletContext());
            applicationContext.refresh();

            // initialize start Spring context
            servletContext.addConfiguration();
            servletContext.addEventListener(new ContextLoaderListener(applicationContext));

            // ToDo: Finde heraus, wie man hier PlayerService injezieren kann!!!
            servletContext.addServlet(PlayerServlet.class, "/players/*");
            servletContext.addServlet(PlayerUnderContractServlet.class, "/contracts/*");
            servletContext.addServlet(TeamServlet.class, "/teams/*");
            servletContext.addServlet(LeagueServlet.class, "/leagues/*");
            servletContext.addServlet(HealthServlet.class, "/health/*");
            servletContext.addServlet(PlayerUnderContractServlet.class, null);
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

        } catch (InternalServerError e) {
            log.error("something went wrong during startup. Will shutdown server!");
            if (server != null && server.isStarted()) {
                try {
                    server.stop();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (BeanCreationException e) {
            log.error("could not create a required bean!");
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void populateAddresses() {
        log.info("population of addresses started!");
        List<List<String>> records = new ArrayList();
        List<Address> addressList = new ArrayList<>();
        EntityManagerFactory entityManagerFactory = StaticApplicationConfiguration.ENTITY_MANAGER_FACTORY;
        try (CSVReader csvReader =
                     new CSVReaderBuilder(
                             new FileReader(
                                     App.class.getClassLoader().getResource("addresses.csv").getFile()))
                             .withSkipLines(1)
                             .build()) {
            try (var entityManager = entityManagerFactory.createEntityManager()) {

                String[] values = null;
                int counter = 0;
                while ((values = csvReader.readNext()) != null && counter < 82727) {
                    records.add(Arrays.asList(values));
                    var addressToStore = CsvEntryParser.mapFromCsvToAddress(values);
                    addressList.add(addressToStore);
                    entityManager.persist(addressToStore);
                    counter++;
                    if (counter % 1000 == 0 && counter > 49103) {
                        log.info("flushing occured. Counter: {}", counter);
                        EntityTransaction transaction = entityManager.getTransaction();
                        transaction.begin();
                        entityManager.flush();
                        transaction.commit();
                    }
                }
                log.info("population of addresses finished!");
            }
        } catch (FileNotFoundException | CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateTeamAddresses(String fileName) {
        log.info("population of team addresses started!");
        EntityManagerFactory entityManagerFactory = StaticApplicationConfiguration.ENTITY_MANAGER_FACTORY;

        try (CSVReader csvReader =
                     new CSVReaderBuilder(
                             new FileReader(
                                     App.class.getClassLoader().getResource(fileName).getFile()))
                             .withSkipLines(1)
                             .build()) {
            try (var entityManager = entityManagerFactory.createEntityManager()) {

                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    var addressToStore = TeamAddressCsvRecordParser.mapFromCsvToAddress(values);
                    EntityTransaction transaction = entityManager.getTransaction();
                    transaction.begin();
                    entityManager.persist(addressToStore);
                    transaction.commit();
                }
                log.info("population of addresses finished!");
            }
        } catch (FileNotFoundException | CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class TeamAddressCsvRecordParser {
        private final static List<String> englishCities = Arrays.asList(
                "Bolton",
                "London",
                "Liverpool",
                "Manchester",
                "Newcastle upon Tyne",
                "Norwich",
                "Stoke-on-Trent");

        static Address mapFromCsvToAddress(String[] column) {
            return englishCities.contains(column[1]) ?
                    new Address(column[1], column[0], column[2], Country.ENGLAND) :
                    new Address(column[1], column[0], column[2], Country.GERMANY);
        }
    }
}
