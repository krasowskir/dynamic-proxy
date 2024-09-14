package org.richard.home;

import groovy.lang.IntRange;
import org.richard.home.domain.Player;
import org.richard.home.service.dto.Country;
import org.richard.home.service.dto.PlayerDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TestPlayerGenerator {

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

    public static PlayerDTO generatePlayerDTO() {
        return new PlayerDTO(
                random.ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString(),
                random.nextInt(13, 99),
                random.ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString(),
                LocalDate.ofEpochDay(startDate + random.nextLong(endDate - startDate + 1)),
                Country.GERMANY
        );
    }

}
