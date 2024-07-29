package org.richard.home.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.richard.home.repository.JpaPlayerRepository;
import org.richard.home.repository.PlayerRepository;
import org.richard.home.service.LocalPlayerService;
import org.richard.home.service.PlayerService;
import org.richard.home.service.TeamService;
import org.richard.home.service.TeamServiceImpl;
import org.richard.home.web.mapper.TeamMapper;

public class StaticApplicationConfiguration {

    public static ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static EntityManagerFactory ENTITY_MANAGER_FACTORY = GeneralConfiguration.entityManagerFactory();

    public static ConstraintValidatorFactory CONSTRAINT_VALIDATOR_FACTORY = VALIDATOR_FACTORY.getConstraintValidatorFactory();

    public static TeamMapper TEAM_MAPPER_INSTANCE = new TeamMapper();

    public static TeamService TEAM_SERVICE_INSTANCE = new TeamServiceImpl(ENTITY_MANAGER_FACTORY, TEAM_MAPPER_INSTANCE);
    public static PlayerRepository PLAYER_REPOSITORY = new JpaPlayerRepository();
    public static PlayerService PLAYER_SERVICE_INSTANCE = new LocalPlayerService(ENTITY_MANAGER_FACTORY, PLAYER_REPOSITORY);

}
