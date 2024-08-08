package org.richard.home.web;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;
import org.richard.home.web.dto.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.Matchers.*;

class PlayerServletIT {

    private static Logger log = LoggerFactory.getLogger(PlayerServletIT.class);

    @Test
    void testGetPlayerByName() {
        String responseBody = RestAssured
                .given()
                .get("/api/players?name=Maximilian%20Braune")
                .asString();
        log.info("response: {}", responseBody);

        RestAssured
                .given()
                .get("/api/players?name=Maximilian%20Braune")
                .then()
                .body(
                        "id", equalTo(177251),
                        "name", equalTo("Maximilian Braune"),
                        "dateOfBirth", hasItems(2003, 7, 6),
                        "countryOfBirth", equalTo(Country.GERMANY.name())
                );
    }

    @Test
    void testGetPlayerByNameInPath() {
        RestAssured
                .given()
                .header(new Header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"))
                .get("/api/players/Maximilian%20Braune")
                .then()
                .statusCode(400)
                .body(stringContainsInOrder("path used:", "is not appropriate!"));
    }

    @Test
    void testGetPlayerByNameContentTypeUrlEncoded() {
        String responseBody = RestAssured
                .given()
                .header(new Header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"))
                .get("/api/players?name=Maximilian%20Braune")
                .asString();
        log.info("response: {}", responseBody);

        RestAssured
                .given()
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"))
                .get("/api/players?name=Maximilian%20Braune")
                .then()
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

}