package org.richard.home.service;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.richard.home.config.DatabaseConfiguration;
import org.richard.home.repository.JpaPlayerRepository;
import org.richard.home.repository.PlayerRepository;
import org.richard.home.web.dto.Country;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaPlayerServiceTestIT {

    PlayerService objectUnderTest;
    PlayerRepository playerRepository;
    private static EntityManagerFactory entityManagerFactory;
    private static DatabaseConfiguration databaseConfiguration;

    @BeforeAll
    private static void setupInfrastructure() {
        databaseConfiguration = new DatabaseConfiguration();
        entityManagerFactory = databaseConfiguration.getEntityManagerFactory();
    }

    @Test
    void testStoringPlayersConcurrently() {
        // given
        this.objectUnderTest = new JpaPlayerService(entityManagerFactory, new JpaPlayerRepository(entityManagerFactory));
        var playerId = 6601;
        var playerName = "Sebastian Andersson";
        var playerPosition = "Attacker";
        var playerCountry = Country.SWEDEN;
        var age = 30;
        var birthDate = LocalDate.of(1991,7, 15);

        // when
        var foundPlayer = objectUnderTest.findPlayerById("6601");
        assertEquals(foundPlayer.getId(),playerId);
        assertEquals(foundPlayer.getName(), playerName);
        assertEquals(foundPlayer.getPosition(), playerPosition);
        assertEquals(foundPlayer.getCountryOfBirth().toUpperCase(), playerCountry.name().toUpperCase());
        assertEquals(foundPlayer.getAlter(), age);
        assertEquals(foundPlayer.getDateOfBirth(), birthDate);
    }

}