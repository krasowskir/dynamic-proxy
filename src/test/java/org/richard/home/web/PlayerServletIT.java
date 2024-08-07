package org.richard.home.web;

import io.restassured.RestAssured;
import net.sf.cglib.core.Local;
import org.junit.jupiter.api.Test;
import org.richard.home.web.dto.Country;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;

class PlayerServletIT {

    @Test
    void testGetPlayerByName() {
        RestAssured
                .given().headers(Map.of("Content-Type","application/x-www-form-urlencoded"))
                .get("/api/players?name=Maximilian%20Braune")
                .then()
                .statusCode(200)
                .and()
                .body(
                        "id", equalTo(177251),
                        "name", equalTo("Maximilian Braune"),
                        "dateOfBirth", hasItems(2003,7,6),
                        "countryOfBirth", equalTo(Country.GERMANY.name())
                );
    }
}