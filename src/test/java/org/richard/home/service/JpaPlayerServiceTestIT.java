package org.richard.home.service;

import groovy.lang.IntRange;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.richard.home.config.DatabaseConfiguration;
import org.richard.home.domain.Player;
import org.richard.home.repository.JpaPlayerRepository;
import org.richard.home.service.dto.Country;
import org.richard.home.service.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class JpaPlayerServiceTestIT {

    private static EntityManagerFactory entityManagerFactory;
    private static DatabaseConfiguration databaseConfiguration;
    private static Logger log = LoggerFactory.getLogger(JpaPlayerServiceTestIT.class);
    PlayerService objectUnderTest;

    @BeforeAll
    private static void setupInfrastructure() {
        databaseConfiguration = new DatabaseConfiguration();
        entityManagerFactory = databaseConfiguration.getEntityManagerFactory();
    }

    @Test
    void testObtainingPlayers() {
        // given
        this.objectUnderTest = new JpaPlayerService(entityManagerFactory, new JpaPlayerRepository(entityManagerFactory));
        var playerId = 6601;
        var playerName = "Sebastian Andersson";
        var playerPosition = "Attacker";
        var playerCountry = Country.SWEDEN;
        var age = 30;
        var birthDate = LocalDate.of(1991, 7, 15);

        // when
        var foundPlayer = objectUnderTest.findPlayerById("6601");
        assertEquals(foundPlayer.getId(), playerId);
        assertEquals(foundPlayer.getName(), playerName);
        assertEquals(foundPlayer.getPosition(), playerPosition);
        assertEquals(foundPlayer.getCountryOfBirth().toUpperCase(), playerCountry.name().toUpperCase());
        assertEquals(foundPlayer.getAlter(), age);
        assertEquals(foundPlayer.getDateOfBirth(), birthDate);
    }

    @Test
    void testStoringPlayersConcurrently() throws InterruptedException {
        // given
        this.objectUnderTest = new JpaPlayerService(entityManagerFactory, new JpaPlayerRepository(entityManagerFactory));

        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        BiFunction<PlayerService, Player, Player> storePlayer = PlayerService::savePlayer;
        List<Callable<Player>> tasks = new ArrayList<>();

        List<Player> expectedPlayerList = provideTestPlayers(10000);

        new IntRange(1, 10000).forEach(item -> tasks.add(() -> storePlayer.apply(objectUnderTest, expectedPlayerList.get(item))));

        List<Future<Player>> resultList = threadPool.invokeAll(tasks);
        resultList.forEach(player -> {
            try {
                Player foundPlayer = Objects.requireNonNull(objectUnderTest.findPlayerById(player.get().getId().toString()));
                boolean comparisonResult = assertPlayerEquality(player.get(), foundPlayer);
                if (!comparisonResult) {
                    log.warn("comparisonResult was false!");
                }
                assertTrue(comparisonResult);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                log.error("NPE");
                throw new RuntimeException(e);
            }

        });
    }


    @Test
    void testStoringAndUpdatingPlayersConcurrently() throws InterruptedException {
        // given
        this.objectUnderTest = new JpaPlayerService(entityManagerFactory, new JpaPlayerRepository(entityManagerFactory));
        var amountOfPlayers = 1000;
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        List<Player> expectedPlayerList = provideTestPlayers(amountOfPlayers);
        List<Callable<Player>> storeTasks = new ArrayList<>();
        List<Callable<Player>> updateTasks = new ArrayList<>();

        // and
        BiFunction<PlayerService, Player, Player> storePlayer = PlayerService::savePlayer;
        BiFunction<PlayerService, Map<String, PlayerDTO>, Player> updatePlayer = PlayerService::updatePlayerById;

        // storing players
        expectedPlayerList.forEach(player -> storeTasks.add(() -> storePlayer.apply(objectUnderTest, player)));
        threadPool.invokeAll(storeTasks);

        // updating players
        List<Map.Entry<String, PlayerDTO>> expectedPlayerUpdates = providePlayerUpdates(expectedPlayerList);
        expectedPlayerUpdates.forEach(entry -> updateTasks.add(() -> updatePlayer.apply(objectUnderTest, Map.of(entry.getKey(), entry.getValue()))));
        threadPool.invokeAll(updateTasks);

        expectedPlayerUpdates.forEach(playerToBe -> {
            try {
                Player foundPlayer = Objects.requireNonNull(objectUnderTest.findPlayerById(playerToBe.getKey()));
                boolean comparisonResult = assertPlayerUpdated(playerToBe.getValue(), foundPlayer);
                if (!comparisonResult) {
                    log.warn("comparisonResult was false!");
                }
                assertTrue(comparisonResult);
            } catch (NullPointerException e) {
                log.error("NPE");
                throw new RuntimeException(e);
            }

        });
    }


    boolean assertPlayerEquality(Player expected, Player provided) {
        if (expected.getId().equals(provided.getId()) &&
                expected.getName().equals(provided.getName()) &&
                expected.getAlter().intValue() == provided.getAlter().intValue() &&
                expected.getDateOfBirth().isEqual(provided.getDateOfBirth()) &&
                expected.getPosition().equals(provided.getPosition()) &&
                expected.getCountryOfBirth().equals(provided.getCountryOfBirth())) {
            return true;
        } else {
            log.warn("difference found!\n expected: {}, \n provided: {}", expected, provided);
            log.warn("provided age: {}, expected age: {}; result: {}", provided.getAlter(), expected.getAlter(), expected.getAlter().intValue() == provided.getAlter().intValue());
            log.warn("provided name: {}, expected name: {}, result: {}", provided.getName(), expected.getName(), expected.getName().equals(provided.getName()));
            log.warn("provided id: {}, expected id: {}; result: {}", provided.getId(), expected.getId(), expected.getId().equals(provided.getId()));
            log.warn("provided date: {}, expected date: {}, result: {}", provided.getDateOfBirth(), expected.getDateOfBirth(), provided.getDateOfBirth().isEqual(expected.getDateOfBirth()));
            log.warn("provided position: {}, expected position: {}, result: {}", provided.getPosition(), expected.getPosition(), expected.getPosition().equals(provided.getPosition()));
            log.warn("provided country: {}, expected country: {}; result: {}", provided.getCountryOfBirth(), expected.getCountryOfBirth(), provided.getCountryOfBirth().equals(expected.getCountryOfBirth()));
            return false;
        }
    }

    boolean assertPlayerUpdated(PlayerDTO expected, Player provided) {
        if (expected.getName().equals(provided.getName()) &&
                expected.getAge() == provided.getAlter().intValue() &&
                expected.getDateOfBirth().isEqual(provided.getDateOfBirth()) &&
                expected.getPosition().equals(provided.getPosition()) &&
                expected.getCountryOfBirth().name().equals(provided.getCountryOfBirth())) {
            return true;
        } else {
            log.warn("difference found!\n expected: {}, \n provided: {}", expected, provided);
            log.warn("provided age: {}, expected age: {}; result: {}", provided.getAlter(), expected.getAge(), expected.getAge() == provided.getAlter());
            log.warn("provided name: {}, expected name: {}, result: {}", provided.getName(), expected.getName(), expected.getName().equals(provided.getName()));
            log.warn("provided date: {}, expected date: {}, result: {}", provided.getDateOfBirth(), expected.getDateOfBirth(), provided.getDateOfBirth().isEqual(expected.getDateOfBirth()));
            log.warn("provided position: {}, expected position: {}, result: {}", provided.getPosition(), expected.getPosition(), expected.getPosition().equals(provided.getPosition()));
            log.warn("provided country: {}, expected country: {}; result: {}", provided.getCountryOfBirth(), expected.getCountryOfBirth().name(), provided.getCountryOfBirth().equals(expected.getCountryOfBirth().name()));
            return false;
        }
    }

    private List<Player> provideTestPlayers(int amount) {
        return new TestPlayerGenerator().generatePlayerResultList(amount);
    }

    private List<Map.Entry<String, PlayerDTO>> providePlayerUpdates(List<Player> fromPlayers) {
        return new TestPlayerGenerator().generatePlayerDTOList(fromPlayers);
    }

    private class TestPlayerGenerator {

        private final static int leftLimit = 97; // letter 'a'
        private final static int rightLimit = 122; // letter 'z'
        private final static int targetStringLength = 10;
        private final static Random random = new Random();
        private final static long startDate = LocalDate.of(1980, 1, 1).toEpochDay();
        private final static long endDate = LocalDate.of(2024, 1, 1).toEpochDay();

        public TestPlayerGenerator() {
        }

        public List<Player> generatePlayerResultList(int amount) {
            return new IntRange(0, amount).stream()
                    .map(item ->
                            new Player(
                                    random.ints(leftLimit, rightLimit + 1)
                                            .limit(targetStringLength)
                                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                            .toString(),
                                    random.nextInt(),
                                    random.ints(leftLimit, rightLimit + 1)
                                            .limit(targetStringLength)
                                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                            .toString(),
                                    LocalDate.ofEpochDay(startDate + random.nextLong(endDate - startDate + 1)),
                                    org.richard.home.domain.Country.GERMANY,
                                    null
                            )).collect(Collectors.toList());
        }

        public List<Map.Entry<String, PlayerDTO>> generatePlayerDTOList(List<Player> players) {
            return players.stream()
                    .map(item -> Map.entry(item.getId().toString(),
                            new PlayerDTO(
                                    random.ints(leftLimit, rightLimit + 1)
                                            .limit(targetStringLength)
                                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                            .toString(),
                                    random.nextInt(),
                                    random.ints(leftLimit, rightLimit + 1)
                                            .limit(targetStringLength)
                                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                            .toString(),
                                    LocalDate.ofEpochDay(startDate + random.nextLong(endDate - startDate + 1)),
                                    Country.GERMANY
                            )))
                    .collect(Collectors.toList());
        }

    }
}