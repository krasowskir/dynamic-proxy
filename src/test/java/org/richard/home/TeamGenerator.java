package org.richard.home;

import org.richard.home.service.dto.AddressDTO;
import org.richard.home.service.dto.Country;
import org.richard.home.service.dto.TeamDTO;

import java.time.LocalDate;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class TeamGenerator {

    private final static int a_letter = 97; // letter 'a'
    private final static int z_letter = 122; // letter 'z'

    private final static int number_0 = 48; // letter 'z'
    private final static int number_9 = 57; // letter 'z'
    private final static int teamNameLength = 12;
    private final static int ownerLength = 8;
    private final static Random random = new Random();
    private final static long startDate = LocalDate.of(1980, 1, 1).toEpochDay();
    private final static long endDate = LocalDate.of(2024, 1, 1).toEpochDay();

    public TeamGenerator() {
    }

    public static Function<String, TeamDTO> generateTeamDTO = (leagueId) ->
            new TeamDTO(
                    random.ints(a_letter, z_letter + 1)
                            .limit(teamNameLength)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString(),
                    random.nextInt(1000000, 10000000),
                    "http://logo-abc.de",
                    random.ints(a_letter, z_letter + 1)
                            .limit(ownerLength)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString(),
                    "MUN",
                    new AddressDTO("Altenberg", "Walter-Richter-Straße", "01773", 11, Country.GERMANY),
                    "0151/".concat(random.ints(number_0, number_9)
                            .limit(8)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString()),
                    "www.test-website.de",
                    random.ints(a_letter, z_letter + 1)
                            .limit(5)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString()
                            .concat("@")
                            .concat(random.ints(a_letter, z_letter + 1)
                                    .limit(4)
                                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                    .toString())
                            .concat(".com"),
                    "xyz",
                    99,
                    leagueId
            );

}
