package org.richard.home.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.richard.home.infrastructure.exception.InternalServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.sql.Connection.TRANSACTION_SERIALIZABLE;
import static org.hibernate.cfg.AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;
import static org.hibernate.cfg.JdbcSettings.ISOLATION;
import static org.hibernate.cfg.PersistenceSettings.*;


/**
 * org.richard.home.config.DatabaseConfiguration
 * is able to be instantiated and return the EntityManagerFactory as
 * SINGLETON. It is flexible because tests can instantiate this class and call getEntityManagerFactory() in order to
 * retrieve the entityManagerFactory.
 */
public class DatabaseConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final static String HOST, PORT, USERNAME, PASSWORD, DATABASE_NAME;
    private static EntityManagerFactory entityManagerFactory;

    static {
        Properties props = new Properties();
        try {
            props.load(DatabaseConfiguration.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        HOST = props.getProperty("database.db.host");
        PORT = props.getProperty("database.db.port");
        USERNAME = props.getProperty("database.db.username");
        PASSWORD = props.getProperty("database.db.password");
        DATABASE_NAME = props.getProperty("database.db.database_name");
    }

    public DatabaseConfiguration() {
        try {
            if (DatabaseConfiguration.entityManagerFactory == null) {
                DatabaseConfiguration.entityManagerFactory = constructEntityManagerFactory();
            }
        } catch (InternalServerError e) {
            log.error("could not create entity manager factory! {}", e.getMessage());
            throw e;
        }
    }

    public static DataSource hikariDataSource() throws SQLException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("cookbook");
        dataSource.setMaximumPoolSize(200);
        dataSource.setMinimumIdle(2);
        dataSource.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 512);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 1024);
        dataSource.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        dataSource.setDriverClassName("org.postgresql.Driver");
        String jdbcUrl = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        dataSource.setAutoCommit(false);
        log.info("configuration: url {}, username: {}", jdbcUrl, USERNAME);
        dataSource.getConnection().isValid(1000);
        return dataSource;
    }

    private static EntityManagerFactory constructEntityManagerFactory() {
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.format_sql", "true");
        jpaProps.put("hibernate.hbm2ddl.auto", "none");
        jpaProps.put("hibernate.show_sql", "true");
        jpaProps.put("hibernate.enable_lazy_load_no_trans", "true");
        jpaProps.put("hibernate.generate_statistics", "true");
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        PersistenceUnitInfo persistenceUnitInfo = myPersistenceUnitInfo();
        jpaProps.put(ISOLATION, TRANSACTION_SERIALIZABLE);
        jpaProps.put(CURRENT_SESSION_CONTEXT_CLASS, "thread");
        jpaProps.put(PERSISTENCE_UNIT_NAME, persistenceUnitInfo.getPersistenceUnitName());
        jpaProps.put(JAKARTA_PERSISTENCE_PROVIDER, persistenceUnitInfo.getPersistenceProviderClassName());
        jpaProps.put(JAKARTA_TRANSACTION_TYPE, persistenceUnitInfo.getTransactionType());
        jpaProps.put("jakarta.persistence.nonJtaDataSource", persistenceUnitInfo.getNonJtaDataSource());

        //cache
        jpaProps.put("hibernate.cache.use_second_level_cache", "false");
//        jpaProps.put("hibernate.cache.use_query_cache", "true");
//        jpaProps.put("hibernate.cache.region.factory_class", "org.redisson.hibernate.RedissonRegionFactory");
//        jpaProps.put("hibernate.cache.redisson.config", "redisson.yaml");
//        jpaProps.put("hibernate.cache.default_cache_concurrency_strategy", "read-write");
//        jpaProps.put("jakarta.persistence.sharedCache.mode","ALL");

        return Optional.ofNullable(
                        new HibernatePersistenceProvider().createContainerEntityManagerFactory(myPersistenceUnitInfo(), jpaProps))
                .orElseThrow(() -> new InternalServerError("entityManagerFactory could not be created! Is database up?", null));
    }

    private static PersistenceUnitInfo myPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "rich-persisten-unit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                try {
                    return hikariDataSource();
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    throw new InternalServerError("There were database Connection issues!", e);
                }
            }

            @Override
            public List<String> getMappingFileNames() {
                return null;
            }

            @Override
            public List<URL> getJarFileUrls() {
                return null;
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                try {
                    return this.getClass().getClassLoader().getResource("org/richard/home/domain").toURI().toURL();
                } catch (MalformedURLException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<String> getManagedClassNames() {
                return null;
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return null;
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return DatabaseConfiguration.entityManagerFactory;
    }

    @Override
    public String toString() {
        return "MyConfiguration{" +
                "host='" + HOST + '\'' +
                ", port='" + PORT + '\'' +
                ", username='" + USERNAME + '\'' +
                ", password='" + PASSWORD + '\'' +
                '}';
    }

}
