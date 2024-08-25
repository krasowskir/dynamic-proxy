package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.richard.home.repository.*;
import org.richard.home.service.*;
import org.richard.home.service.mapper.PlayerMapper;
import org.richard.home.service.mapper.TeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticApplicationConfiguration {

    private static final Logger log = LoggerFactory.getLogger(StaticApplicationConfiguration.class);

    static {
        databaseConfiguration = new DatabaseConfiguration();
    }
    public static ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static ConstraintValidatorFactory CONSTRAINT_VALIDATOR_FACTORY = VALIDATOR_FACTORY.getConstraintValidatorFactory();
    public static TeamMapper TEAM_MAPPER_INSTANCE = new TeamMapper();
    public static PlayerMapper PLAYER_MAPPER_INSTANCE = new PlayerMapper();
    private static DatabaseConfiguration databaseConfiguration;
    public static EntityManagerFactory ENTITY_MANAGER_FACTORY = databaseConfiguration.getEntityManagerFactory();
    public static TeamRepository TEAM_REPOSITORY = new JpaTeamRepository(ENTITY_MANAGER_FACTORY);

    public static TeamService TEAM_SERVICE_INSTANCE = new JpaTeamService(ENTITY_MANAGER_FACTORY, TEAM_MAPPER_INSTANCE, TEAM_REPOSITORY);
    public static PlayerRepository PLAYER_REPOSITORY = new JpaPlayerRepository(ENTITY_MANAGER_FACTORY);
    public static PlayerService PLAYER_SERVICE_INSTANCE = new JpaPlayerService(ENTITY_MANAGER_FACTORY, PLAYER_REPOSITORY);
    public static LeagueRepository LEAGUE_REPOSITORY = new JpaLeagueRepository(ENTITY_MANAGER_FACTORY);
    public static LeagueService LEAGUE_SERVICE = new JpaLeagueService(ENTITY_MANAGER_FACTORY, LEAGUE_REPOSITORY);


    public StaticApplicationConfiguration() {
        log.info("static application configuration instantiated! DatabaseConfiguration: {}", databaseConfiguration);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
