package org.richard.home.web;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerUnderContractServletIT {

    private static final Logger log = LoggerFactory.getLogger(PlayerUnderContractServletIT.class);

    @Nested
    @Tag("GetCurrentTeam")
    class GetCurrentTeam {

        @Test
        void testGetCurrentTeamHappyPath() {
            RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .get("/players/44/contracts")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(66),
                            "name", equalTo("Manchester United FC"));
        }

        @Test
        void testGetCurrentTeamWithoutPlayerIdAfterServletContextWasPopulated() {
            RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .get("/players/44/contracts")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(66),
                            "name", equalTo("Manchester United FC"));

            ExtractableResponse<Response> response = RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .expect()
                    .statusCode(400)
                    .given()
                    .get("/contracts")
                    .then()
                    .extract();

            assertEquals(response.body().asString().trim(), "playerId was null or contained only whitespaces");
        }

        @Test
        void testGetCurrentTeamNotFoundPlayer() {
            var playerId = "0000";
            var response = RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .expect()
                    .statusCode(404)
                    .given()
                    .get(String.format("/players/%s/contracts", playerId))
                    .then()
                    .extract();

            assertEquals(response.body().asString().trim(),
                    String.format("player with playerId: %s could not be matched with any team!", playerId));
        }
    }

    @Nested
    @Tag("PutCurrentTeam")
    class PutCurrentTeam {
        private static String VALID_PLAYER_JSON_BODY = """
                {
                "name": "Richard Johanson",
                        "age": 33,
                        "position": "STRIKER",
                        "dateOfBirth": "1991-06-20",
                        "countryOfBirth": "GERMANY"
                  }
                  """;

        private static String VALID_PLAYER_UPDATE_CONTRACT_JSON_BODY = """
                {
                    "playerId": "%s",
                    "teamId": 66
                  }
                  """;

        @Test
        void testUpdateContractHappyPath() {
            Integer playerId = RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(VALID_PLAYER_JSON_BODY)
                    .expect()
                    .statusCode(201)
                    .given()
                    .post("/api/players")
                    .then()
                    .extract().body().jsonPath()
                    .get("id");

            log.info("created player successful for updating! playerId: {}", playerId);

            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(String.format(VALID_PLAYER_UPDATE_CONTRACT_JSON_BODY, playerId.toString()))
                    .expect()
                    .statusCode(200)
                    .given()
                    .put(String.format("/api/players/%s/contracts", playerId))
                    .then()
                    .extract().body().jsonPath()
                    .get("id");

            RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .get(String.format("/players/%s/contracts", playerId))
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(66),
                            "name", equalTo("Manchester United FC"));
        }


    }

    @Nested
    @Tag("DeleteCurrentTeam")
    class DeleteCurrentTeam {
        private static String VALID_PLAYER_JSON_BODY = """
                {
                "name": "Richard Johanson",
                        "age": 33,
                        "position": "STRIKER",
                        "dateOfBirth": "1991-06-20",
                        "countryOfBirth": "GERMANY"
                  }
                  """;

        private static String VALID_PLAYER_UPDATE_CONTRACT_JSON_BODY = """
                {
                    "playerId": "%s",
                    "teamId": "%s"
                  }
                  """;

        @Test
        void testDeletingContract() {

            Integer playerId = RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(VALID_PLAYER_JSON_BODY)
                    .expect()
                    .statusCode(201)
                    .given()
                    .post("/api/players")
                    .then()
                    .extract().body().jsonPath()
                    .get("id");

            log.info("created player successful for deletion! playerId: {}", playerId);

            String newTeamId = "66";
            Integer updatedTeam = RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .body(String.format(VALID_PLAYER_UPDATE_CONTRACT_JSON_BODY, playerId.toString(), newTeamId))
                    .expect()
                    .statusCode(200)
                    .given()
                    .put(String.format("/api/players/%s/contracts", playerId))
                    .then()
                    .extract().body().jsonPath()
                    .get("currentTeam.id");
            log.info("player: {} has team updated to: {}", playerId, updatedTeam);

            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", "application/json; charset=UTF-8"))
                    .given()
                    .delete(String.format("/api/players/%s/contracts/%s", playerId, newTeamId))
                    .then()
                    .statusCode(200)
                    .body(stringContainsInOrder("Deletion of players contract was successful!"));

        }

    }
}