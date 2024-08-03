package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.richard.home.repository.JpaLeagueRepository;
import org.richard.home.repository.JpaPlayerRepository;
import org.richard.home.repository.LeagueRepository;
import org.richard.home.repository.PlayerRepository;
import org.richard.home.service.*;
import org.richard.home.web.mapper.TeamMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated(since = "do not use this as we need spring context. Use static configuration")
@Configuration
public class ApplicationConfiguration {

    public static ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static EntityManagerFactory ENTITY_MANAGER_FACTORY = GeneralConfiguration.entityManagerFactory();

    public static ConstraintValidatorFactory CONSTRAINT_VALIDATOR_FACTORY = VALIDATOR_FACTORY.getConstraintValidatorFactory();

    public TeamMapper teamMapper() {
        return new TeamMapper();
    }

    @Bean
    public TeamService teamService() {
        return new JpaTeamService(ENTITY_MANAGER_FACTORY, teamMapper());
    }

    @Bean
    public PlayerService playerService() {
        return new JpaPlayerService(ENTITY_MANAGER_FACTORY, playerRepository());
    }

    public PlayerRepository playerRepository() {
        return new JpaPlayerRepository(ENTITY_MANAGER_FACTORY);
    }

    @Bean
    public LeagueService leagueService() {
        return new JpaLeagueService(leagueRepository());
    }

    public LeagueRepository leagueRepository() {
        return new JpaLeagueRepository(ENTITY_MANAGER_FACTORY);
    }
}
