package org.richard.home.web;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;
import org.richard.home.web.dto.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

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

}