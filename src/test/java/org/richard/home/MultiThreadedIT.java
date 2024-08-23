package org.richard.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import groovy.lang.IntRange;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.richard.home.service.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Disabled
@Tag("multithreaded")
public class MultiThreadedIT {

    private static final Logger log = LoggerFactory.getLogger(MultiThreadedIT.class);
    private static String VALID_PLAYER_JSON_BODY = """
            {
            "name": "Richard Johanson",
                    "age": 33,
                    "position": "STRIKER",
                    "dateOfBirth": "1991-06-20",
                    "countryOfBirth": "GERMANY"
              }
              """;

    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
}
