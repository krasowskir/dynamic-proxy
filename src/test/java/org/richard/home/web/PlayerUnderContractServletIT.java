package org.richard.home.web;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

class PlayerUnderContractServletIT {

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

            RestAssured.given()
                    .baseUri("http://localhost:8080/api")
                    .get("/contracts")
                    .then()
                    .statusCode(400)
                    .and()
                    .body(stringContainsInOrder("playerId was null or contained only whitespaces"));
        }
    }
}