package org.richard.home.web;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Map;
import java.util.stream.Stream;

class MyArgumentsProvider implements ArgumentsProvider {
    private static final String PLAYER_JSON_EMPTY_NAME = """
            {
              "name": "",
              "age": 33,
              "position": "STRIKER",
              "dateOfBirth": "1991-06-20",
              "countryOfBirth": "GERMANY"
            }
            """;
    private static final String PLAYER_JSON_BAD_AGE = """
            {
              "name": "Richard Johanson",
              "age": 10,
              "position": "STRIKER",
              "dateOfBirth": "1991-06-20",
              "countryOfBirth": "GERMANY"
            }
            """;

    private static final String PLAYER_JSON_EMPTY_POSITION = """
            {
              "name": "Richard Johanson",
              "age": 10,                 
              "dateOfBirth": "1991-06-20",
              "countryOfBirth": "GERMANY"
            }
            """;

    private static final String PLAYER_JSON_EMPTY_BIRTHDATE = """
            {
              "name": "Richard Johanson",
              "age": 10,                 
              "dateOfBirth": "",
              "countryOfBirth": "GERMANY"
            }
            """;

    private static final String PLAYER_JSON_WRONG_COUNTRY = """
            {
              "name": "Richard Johanson",
              "age": 10,                 
              "dateOfBirth": "1991-06-20",
              "countryOfBirth": "AlemaNiaaa"
            }
            """;

    private static final String BROKEN_JSON = """
            {
              "name": "Richard Johanson",
              "agBROKEN JSON
            }
            """;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Map.of(
                PLAYER_JSON_EMPTY_NAME, "name cannot be null or empty",
                PLAYER_JSON_BAD_AGE, "age must be higher or at least 12",
                PLAYER_JSON_EMPTY_POSITION, "darf nicht leer sein",
                PLAYER_JSON_EMPTY_BIRTHDATE, "darf nicht leer sein",
                PLAYER_JSON_WRONG_COUNTRY, "Cannot deserialize value of type `org.richard.home.web.dto.Country` from String \"AlemaNiaaa\"",
                BROKEN_JSON, "Illegal unquoted character"
        ).entrySet().stream().map(Arguments::of);
    }
}
