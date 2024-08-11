package org.richard.home.web.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.richard.home.web.WebUtils;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebUtilsTest {


    @Test
    void testHandleInvalidPath_filters_out() throws IOException {
        // given
        var notMatchingPath = "http://localhost:8080/api/playe";
        var validPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(notMatchingPath);

        // expect
        Assertions.assertThrows(IllegalStateException.class, () -> WebUtils.handleInvalidPath(mockedRequest, validPath));

    }

    @Test
    void testHandleInvalidPath_trickyPattern() throws IOException {
        // given
        var uri = "http://localhost:8080/api/players/12345";
        var misleadPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(uri);

        // expect
        Assertions.assertThrows(IllegalStateException.class, () -> WebUtils.handleInvalidPath(mockedRequest, misleadPath));
    }

    @Test
    void testHandleInvalidPath_happyPath() throws IOException {
        // given
        var uri = "http://localhost:8080/api/players";
        var validPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(uri);

        // when
        assertDoesNotThrow(() -> WebUtils.handleInvalidPath(mockedRequest, validPath));
    }

    @Test
    void testFiltersCharsetOut_happyPath() {
        var contentTypeVeaderValue = "application/x-www-form-urlencoded; charset=UTF-8";
        assertTrue(contentTypeVeaderValue.contains("charset"));
        var result = WebUtils.stripCharset(contentTypeVeaderValue);
        assertFalse(result.contains("charset"));
        assertTrue("application/x-www-form-urlencoded".equals(result));
    }

    @Test
    void testFiltersCharsetOut() {
        var contentTypeVeaderValue = "application/x-www-form-urlencoded";
        assertFalse(contentTypeVeaderValue.contains("charset"));
        var result = WebUtils.stripCharset(contentTypeVeaderValue);
        assertFalse(result.contains("charset"));
        assertTrue(contentTypeVeaderValue.equals(result));
    }

    @Test
    void testExtractPlayerName_happyPath() {
        var playerName = "Richard Johanson";
        var parameterName = "name";
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getParameter("name")).thenReturn(playerName);

        String extractedName = WebUtils.extractRequestParam(mockedRequest, parameterName);
        assertTrue(playerName.equals(extractedName));
    }

    @Test
    void testExtractPlayerName_startsWithSlash() {
        var playerName = "/Richard%20Johanson";
        var parameterName = "name";
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getParameter("name")).thenReturn(playerName);

        String extractedName = WebUtils.extractRequestParam(mockedRequest, parameterName);
        assertFalse(playerName.equals(extractedName));
        assertTrue(extractedName.equals("Richard Johanson"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void testExtractPlayerId(String input){
        // given
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn("http://localhost:8080/api/players/".concat(input));

        // expect
        Assertions.assertNull(WebUtils.extractPlayerId(mockedRequest, "/api/players/.*"));

        // and
        var secondMockedRequest = mock(HttpServletRequest.class);
        when(secondMockedRequest.getRequestURI()).thenReturn("http://localhost:8080/api/players".concat(input));
        Assertions.assertNull(WebUtils.extractPlayerId(mockedRequest, "/api/players/.*"));
    }

    @ParameterizedTest
    @ArgumentsSource(ContentTypeProvider.class)
    void testHandleBadContentType(Map.Entry<String, Object[]> args) {

        // given
        String providedContentType = args.getKey();
        String expectedContentType = String.valueOf(args.getValue()[0]);
        Boolean expectedResult = Boolean.parseBoolean(String.valueOf(args.getValue()[1]));

        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getContentType()).thenReturn(providedContentType);

        if (expectedResult){
            assertDoesNotThrow(() -> WebUtils.handleBadContentType(mockedRequest, expectedContentType));
        } else {
            assertThrows(IllegalArgumentException.class,
                    () -> WebUtils.handleBadContentType(mockedRequest, expectedContentType));
        }
    }
}