package org.richard.home.web;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.richard.home.web.dto.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.Matchers.*;

// ToDo: startet aktuell alles. Webschicht sollte isoliert vertestet werden!
class PlayerServletIT {
    private static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String INVALID_CONTENT_TYPE = "invalid-contentType";

    private static Logger log = LoggerFactory.getLogger(PlayerServletIT.class);

    @Nested
    @Tag("GetPlayers")
    class GetPlayers {


        @Test
        void testGetPlayerByNameWithoutContentType() {
            RestAssured
                    .given()
                    .get("/api/players?name=Maximilian%20Braune")
                    .then()
                    .statusCode(200)
                    .body(
                            "id", equalTo(177251),
                            "name", equalTo("Maximilian Braune"),
                            "dateOfBirth", hasItems(2003, 7, 6),
                            "countryOfBirth", equalTo(Country.GERMANY.name())
                    );
        }

        @ParameterizedTest
        @ValueSource(strings = {FORM_URL_ENCODED_CONTENT_TYPE, INVALID_CONTENT_TYPE})
        void testGetPlayerByNameInPath(String contentType) {
            RestAssured
                    .given()
                    .header(new Header("Content-Type", contentType))
                    .get("/api/players/Maximilian%20Braune")
                    .then()
                    .statusCode(400);
//                    .body(stringContainsInOrder("path used:", "is not appropriate!"));
            //                    .body(stringContainsInOrder("Unexpected value:", "invalid-contentType"));
        }


        @Test
        void testGetPlayerByNameContentTypeUrlEncoded() {
            RestAssured
                    .given()
                    .headers(Map.of("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"))
                    .get("/api/players?name=Maximilian%20Braune")
                    .then()
                    .statusCode(200)
                    .and()
                    .header("Content-Type", "application/json")
                    .body(
                            "id", equalTo(177251),
                            "name", equalTo("Maximilian Braune"),
                            "dateOfBirth", hasItems(2003, 7, 6),
                            "countryOfBirth", equalTo(Country.GERMANY.name())
                    );
        }

        @Test
        void testGetPlayerByIdContentTypeJson() {
            RestAssured.given()
                    .baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .get("/api/players/177251")
                    .then()
                    .statusCode(200)
                    .and()
                    .header("Content-Type", "application/json")
                    .body(
                            "id", equalTo(177251),
                            "name", equalTo("Maximilian Braune"),
                            "dateOfBirth", hasItems(2003, 7, 6),
                            "countryOfBirth", equalTo(Country.GERMANY.name())
                    );
        }

        @Test
        void testGetPlayerByIdNotFound() {
            RestAssured.given()
                    .baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .get("/api/players/000000000")
                    .then()
                    .statusCode(404)
                    .and()
                    .header("Content-Type", "application/json")
                    .body(stringContainsInOrder("no player found "));
        }
    }

    @Nested
    @Tag("PostPlayers")
    class PostPlayers {

        private static String VALID_PLAYER_JSON_BODY = """
                {
                "name": "Richard Johanson",
                        "age": 33,
                        "position": "STRIKER",
                        "dateOfBirth": "1991-06-20",
                        "countryOfBirth": "GERMANY"
                  }
                  """;

        @ParameterizedTest
        @ValueSource(strings = {FORM_URL_ENCODED_CONTENT_TYPE, INVALID_CONTENT_TYPE})
        void testCreatePlayerWithBadContentType(String contentType) {
            RestAssured.given()
                    .baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", contentType))
                    .post("/api/players")
                    .then()
                    .statusCode(400)
                    .and()
                    .body(stringContainsInOrder("invalid content type"));
        }

        @ParameterizedTest
        @ArgumentsSource(MyArgumentsProvider.class)
        void testCreatePlayerFailingValidation(Map.Entry<String, String> arg) {
            RestAssured.given()
                    .baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(arg.getKey())
                    .post("/api/players")
                    .then()
                    .statusCode(400)
                    .and()
                    .body(stringContainsInOrder(arg.getValue()));
        }

        @Test
        void testCreatePlayerHappyPath() {
            RestAssured.given()
                    .baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(VALID_PLAYER_JSON_BODY)
                    .post("/api/players")
                    .then()
                    .statusCode(201)
                    .and()
                    .body(stringContainsInOrder("\"name\":\"Richard Johanson\",\"alter\":33,\"position\":\"STRIKER\",\"dateOfBirth\":[1991,6,20],\"countryOfBirth\":\"GERMANY\""));
        }
    }

}