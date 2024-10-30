package org.richard.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import groovy.lang.IntRange;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.domain.Team;
import org.richard.home.service.dto.AddressDTO;
import org.richard.home.service.dto.Country;
import org.richard.home.service.dto.PlayerDTO;
import org.richard.home.service.dto.TeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Disabled
@Tag("multithreaded")
public class MultiThreadedIT {

    private static final Logger log = LoggerFactory.getLogger(MultiThreadedIT.class);

    private static final String PLAYER_ID = "playerId";
    private static final Random rand = new Random();
    private static final ConcurrentHashMap<Object, Object> resultStore = new ConcurrentHashMap<>(10000);
    private static final HttpRequest.Builder builder = HttpRequest.newBuilder();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String VALID_PLAYER_JSON_BODY = """
            {
            "name": "Richard Johanson",
                    "age": 33,
                    "position": "STRIKER",
                    "dateOfBirth": "1991-06-20",
                    "countryOfBirth": "GERMANY"
              }
              """;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Callable<Boolean> executeCall(HttpClient httpClient, HttpRequest request, ConcurrentSkipListSet<String> playersList) {
        return () -> {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                var playerCreated = objectMapper.treeToValue(objectMapper.readTree(response.body()), PlayerDTO.class);
                var playerId = objectMapper.readTree(response.body()).get("id").asText();
                playersList.add(playerId);
                log.info("player: {} created: {}", playerId, playerCreated);
                return true;
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
                return false;
            }
        };
    }

    private final Supplier<PlayerDTO> createPlayerTestData = TestPlayerGenerator::generatePlayerDTO;

    private final Function<HttpRequest, String> makeHttpRequest = (request) -> {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    private final Function<PlayerDTO, HttpRequest> convertToCreatePlayerHttpRequest = (playerDTO) -> {
        try {
            return builder
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(new URI("http://localhost:8080/api/players"))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(playerDTO), StandardCharsets.UTF_8))
                    .build();
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    private final Function<TeamDTO, HttpRequest> createTeamHttpRequest = (teamDTO) -> {
        try {
            return builder
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(new URI("http://localhost:8080/api/teams"))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(teamDTO), StandardCharsets.UTF_8))
                    .build();
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    private final Function<Team, HttpRequest> updateContractHttpRequest = (team) -> {
        try {
            ObjectNode updateContractJson = objectMapper.createObjectNode();
            final String playerId = String.valueOf(resultStore.get(Thread.currentThread().getId()));
            updateContractJson.put("playerId", playerId);
            updateContractJson.put("teamId", team.getId());
            return builder
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .uri(new URI(String.format("http://localhost:8080/api/players/%s/contracts", playerId)))
                    .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(updateContractJson), StandardCharsets.UTF_8))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private final Consumer<Player> storePlayerWithTimeInResultStore = (player) -> {
        try {
            ObjectNode playerWithSystemInfo = objectMapper.createObjectNode();
            playerWithSystemInfo.put("player", objectMapper.writeValueAsString(player));
            playerWithSystemInfo.put("threadId", Thread.currentThread().getId());
            playerWithSystemInfo.put("time", String.valueOf(Timestamp.from(Instant.now())));
            resultStore.put(Thread.currentThread().getId(), player.getId());
            resultStore.put(player.getId(), objectMapper.writeValueAsString(playerWithSystemInfo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    };
    private final BiConsumer<String, TeamDTO> storeTeamValuesInResultStore = resultStore::put;

    private final Function<Player, PlayerDTO> mapFromDomainToDTO = fromPlayer -> new PlayerDTO(
            fromPlayer.getName(),
            fromPlayer.getAlter(),
            fromPlayer.getPosition(),
            fromPlayer.getDateOfBirth(),
            Country.valueOf(fromPlayer.getCountryOfBirth()));

    private final Function<Address, AddressDTO> mapFromAddressToDTO = fromAddress -> new AddressDTO(
            fromAddress.getCity(), fromAddress.getStreet(), fromAddress.getPlz(), 22, fromAddress.getCountry().name()
    );
    private final Function<Team, TeamDTO> mapFromDomainToTeamDTO = fromTeam -> new TeamDTO(
            fromTeam.getName(),
            fromTeam.getBudget(),
            fromTeam.getLogo(),
            fromTeam.getOwner(),
            fromTeam.getTla(),
            mapFromAddressToDTO.apply(fromTeam.getAddress()),
            fromTeam.getPhone(),
            fromTeam.getWebsite(),
            fromTeam.getEmail(),
            fromTeam.getVenue(),
            fromTeam.getWyId(),
            String.valueOf(fromTeam.getLeague().getId()));

    private final Function<String, Player> convertResponseToPlayer = (response) -> {
        try {
            return objectMapper.treeToValue(objectMapper.readTree(response), Player.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    private final Function<String, Team> convertResponseToTeam = (response) -> {
        try {
            return objectMapper.treeToValue(objectMapper.readTree(response), Team.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    private final Function<String, Map.Entry<Player, Team>> convertResponseToPlayerWithSquad = (response) -> {
        try {
            var player = objectMapper.treeToValue(objectMapper.readTree(response), Player.class);
            var team = player.getCurrentTeam();
            return Map.entry(player, team);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };
    private final Supplier<String> obtainRandomLeagueId = () -> {
        try {
            var leagueListAsJson = httpClient.send(
                            HttpRequest
                                    .newBuilder(new URI("http://localhost:8080/api/leagues/list"))
                                    .GET()
                                    .version(HttpClient.Version.HTTP_1_1)
                                    .build(),
                            HttpResponse.BodyHandlers.ofString())
                    .body();
            JsonNode listOfLeagues = objectMapper.readTree(leagueListAsJson);
            JsonNode randomLeague = listOfLeagues.get(rand.nextInt(0, listOfLeagues.size() - 1));
            return String.valueOf(randomLeague.get("id"));
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    private final Consumer<Object> logEntity = (object) -> {
        try {
            ObjectNode entityWithSystemInfo = objectMapper.createObjectNode();
            entityWithSystemInfo.put("time", String.valueOf(Timestamp.from(Instant.now())));
            entityWithSystemInfo.put("threadId", Thread.currentThread().getId());
            entityWithSystemInfo.put("entity", objectMapper.writeValueAsString(object));
            log.info(objectMapper.writeValueAsString(entityWithSystemInfo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    @Test
    void testPlayerCreation() throws URISyntaxException, InterruptedException {

        //  given
        HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8080/api/players"))
                .POST(HttpRequest.BodyPublishers.ofString(VALID_PLAYER_JSON_BODY, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json; charset=UTF-8")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        ConcurrentSkipListSet<String> playerIdList = new ConcurrentSkipListSet<>();

        // when
        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        threadPool.invokeAll(new IntRange(0, 10000)
                .stream()
                .map(elem -> executeCall(httpClient, request, playerIdList))
                .collect(Collectors.toList())
        );
        log.info("players stored: {}", String.join(",", playerIdList));


    }

    // ToDo: Scenario könnte mit GET Operation arbeiten um den Zustandsübergang zwischen POST und Get zu vertesten. Nutze
    // lastModified header oder Etag header
    @Test
    void testPlayerContractUpdateScenario() throws InterruptedException {

        var playerDTOGenerator = new TestPlayerGenerator();
        var teamGenerator = new TeamGenerator();

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // task player X creation
        for (int i = 0; i < 10000; i++) {
            Runnable task = () -> {
                Optional.of(createPlayerTestData.get())
                        .stream()
                        .map(convertToCreatePlayerHttpRequest)
                        .map(makeHttpRequest)
                        .map(convertResponseToPlayer)
                        .peek(logEntity)
                        .peek(storePlayerWithTimeInResultStore)
                        .map(player -> obtainRandomLeagueId.get())
                        .map(leagueId -> TeamGenerator.generateTeamDTO.apply(leagueId))
                        .map(createTeamHttpRequest)
                        .map(makeHttpRequest)
                        .map(convertResponseToTeam)
                        .map(updateContractHttpRequest)
                        .map(makeHttpRequest)
                        .map(convertResponseToPlayerWithSquad)
                        .peek(entry -> logEntity.accept(entry.getKey()))
                        .forEach(entry -> storePlayerWithTimeInResultStore.accept(entry.getKey()));
            };
            threadPool.submit(task);
        }

        boolean isFinished = threadPool.awaitTermination(20, TimeUnit.SECONDS);


        // task new team Y creation
        // task player contract update X -> MUN


        // task get and update contract of player


        // task player contract update X -> Y
    }
}
