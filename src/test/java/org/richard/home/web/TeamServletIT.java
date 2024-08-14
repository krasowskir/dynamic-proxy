package org.richard.home.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.richard.home.config.StaticApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.stringContainsInOrder;

class TeamServletIT {
    private static final String CONTENT_TYPE_JSON_VALUE = "application/json; charset=UTF-8";
    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
    private static Logger log = LoggerFactory.getLogger(TeamServletIT.class);
    private static ObjectMapper objectMapper = StaticApplicationConfiguration.OBJECT_MAPPER;

    private static String provideValidTeamJson() {
        return "{\"name\":\"Manchester United FC\",\"budget\":1000000,\"logo\":\"test\",\"owner\":\"1878\",\"tla\":\"MUN\",\"address\":" +
                "\"{\\\"city\\\":\\\"Berlin\\\",\\\"street\\\":\\\"Guentzelstraße\\\",\\\"plz\\\":\\\"10707\\\",\\\"houseNumber\\\":11,\\\"country\\\":\\\"GERMANY\\\"}\"," +
                "\"phone\":\"01234567890\",\"website\":\"www.mun.com\",\"email\":\"test@testi.de\",\"venue\":\"\",\"wyId\":0,\"leagueId\":\"2021\"}";

    }

    private static String provideUpdatedValidTeamJson() {
        return """
                {"name":"Manchester United FC  Updated",
                "budget":999999,
                "logo":"success",
                "owner":"1878",
                "tla":"MUN",
                "address":"{\\"city\\":\\"Manchester\\",\\"street\\":\\"Guentzelstraße\\",\\"plz\\":\\"M60\\",\\"houseNumber\\":11,\\"country\\":\\"ENGLAND\\"}",
                "phone":"01234567890",
                "website":"https://www.manutd.com/en/",
                "email":"accessibility@manutd.co.uk",
                "venue":"",
                "wyId":0,
                "leagueId":"2021"
                }""";

    }

    private static String provideValidTeamJsonButNotExistingLeagueId(String notExistingLeagueId) {

        return String.format("{\"name\":\"Manchester United FC\",\"budget\":1000000,\"logo\":\"test\",\"owner\":\"1878\",\"tla\":\"MUN\",\"address\":" +
                "\"{\\\"city\\\":\\\"Berlin\\\",\\\"street\\\":\\\"Guentzelstraße\\\",\\\"plz\\\":\\\"10707\\\",\\\"houseNumber\\\":11,\\\"country\\\":\\\"GERMANY\\\"}\"," +
                "\"phone\":\"01234567890\",\"website\":\"www.mun.com\",\"email\":\"test@testi.de\",\"venue\":\"\",\"wyId\":0,\"leagueId\":\"%s\"}", notExistingLeagueId);

    }

    private static String provideInvalidTeamJson() {
        // missing street in address
        return "{\"name\":\"Manchester United FC\",\"budget\":1000000,\"logo\":\"test\",\"owner\":\"1878\",\"tla\":\"MUN\",\"address\":" +
                "\"{\\\"city\\\":\\\"Berlin\\\",\\\"plz\\\":\\\"10707\\\",\\\"houseNumber\\\":11,\\\"country\\\":\\\"GERMANY\\\"}\"," +
                "\"phone\":\"01234567890\",\"website\":\"www.mun.com\",\"email\":\"test@testi.de\",\"venue\":\"\",\"wyId\":0,\"leagueId\":\"2021\"}";
    }

    @Tag("GetTeam")
    @Nested
    class GetTeam {

        @Test
        void testGetTeamNullParam() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .get("/api/teams")
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("request parameter id was null"));


        }

        @Test
        void testGetTeamNotANumber() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .get("/api/teams?id=''")
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("For input string: \"''\""));
        }

        @Test
        void testGetTeamNotFound() {
            String notExistingId = "000000000";
            RestAssured.given().baseUri("http://localhost:8080")
                    .get("/api/teams?id=".concat(notExistingId))
                    .then()
                    .statusCode(404)
                    .body(stringContainsInOrder("team with id:", "could not be found!"));
        }

        @Test
        void testGetTeamHappyPath() {
            String idOfExistingTeam = "67";
            RestAssured.given().baseUri("http://localhost:8080")
                    .given()
                    .get("/api/teams?id=".concat(idOfExistingTeam))
                    .then()
                    .statusCode(200)
                    .body(stringContainsInOrder("Newcastle United FC"));
        }


    }

    @Tag("PostTeam")
    @Nested
    class PostTeam {

        @Test
        void testInvalidTeamInJson() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body("{\"name\": \"invalid-JSON")
                    .post("/api/teams")
                    .then()
                    .statusCode(400);
        }

        @Test
        void testFailingValidation() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideInvalidTeamJson())
                    .post("/api/teams")
                    .then()
                    .statusCode(400);
        }

        @Test
        void testCreatingTeamValidJsonHappyPath() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJson())
                    .post("/api/teams")
                    .then()
                    .statusCode(201)
                    .body(stringContainsInOrder("team successfully created.", "TeamId:"));
        }

        @Test
        void testCreatingTeamNotExistingLeagueId() {
            String notExistingLeagueId = "00000000";
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJsonButNotExistingLeagueId(notExistingLeagueId))
                    .post("/api/teams")
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("league specified does not exist!", notExistingLeagueId));
        }

    }

    @Tag("PutTeam")
    @Nested
    class PutTeam {

        @Test
        void testBadContentType() {
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_URL_ENCODED))
                    .body(provideValidTeamJson())
                    .put("/api/teams")
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("invalid content type:"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"/api/teams/", "/api/teams/id", "/api/teams/id/  "})
        void testBadTeamIdProvided(String pathWithoutTeamId) {
            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJson())
                    .put(pathWithoutTeamId)
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("teamId was null"));
        }

        @Test
        void testTeamJsonFailsValidation() {
            String teamId = extractTeamId(RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJson())
                    .post("/api/teams")
                    .asString());


            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideInvalidTeamJson())
                    .put(String.format("/api/teams/id/%s", teamId))
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder("address is not valid"));
        }

        @Test
        void testTeamJsonContainsNotExistingLeagueId() {
            String teamId = extractTeamId(RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJson())
                    .post("/api/teams")
                    .asString());

            String notExistingLeagueId = "00000";

            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJsonButNotExistingLeagueId("00000"))
                    .put(String.format("/api/teams/id/%s", teamId))
                    .then()
                    .statusCode(400)
                    .body(stringContainsInOrder(String.format("league could not be found with id: %s!", notExistingLeagueId)));
        }

        @Test
        void testUpdateTeamHappyPath() {
            String teamId = extractTeamId(RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideValidTeamJson())
                    .post("/api/teams")
                    .asString());
            log.info("team created - teamId: {}", teamId);

            RestAssured.given().baseUri("http://localhost:8080")
                    .header(new Header("Content-Type", CONTENT_TYPE_JSON_VALUE))
                    .body(provideUpdatedValidTeamJson())
                    .put(String.format("/api/teams/id/%s", teamId))
                    .then()
                    .statusCode(200)
                    .body(stringContainsInOrder("Manchester United FC  Updated"));
        }
    }

    private static String extractTeamId(String response) {
        return response.substring(response.indexOf("TeamId: "))
                .trim()
                .replaceAll("\n", "")
                .substring("TeamId: ".length() - 1)
                .stripLeading()
                .stripTrailing();
    }
}