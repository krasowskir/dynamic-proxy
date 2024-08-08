package org.richard.home.web.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.richard.home.web.WebUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WebUtilsTest {

    @Test
    void testHandleInvalidPath_filters_out() throws IOException {
        // given
        var notMatchingPath = "http://localhost:8080/api/playe";
        var validPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(notMatchingPath);

        // and
        var httpResponse = mock(HttpServletResponse.class);

        // expect
        Assertions.assertThrows(IllegalStateException.class, () -> WebUtils.handleInvalidPath(mockedRequest, httpResponse, validPath));

    }

    @Test
    void testHandleInvalidPath_trickyPattern() throws IOException {
        // given
        var uri = "http://localhost:8080/api/players/12345";
        var misleadPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(uri);

        // and
        var httpResponse = mock(HttpServletResponse.class);

        // expect
        Assertions.assertThrows(IllegalStateException.class, () -> WebUtils.handleInvalidPath(mockedRequest, httpResponse, misleadPath));
    }

    @Test
    void testHandleInvalidPath_happyPath() throws IOException {
        // given
        var uri = "http://localhost:8080/api/players";
        var validPath = "/api/players";
        var mockedRequest = mock(HttpServletRequest.class);
        when(mockedRequest.getRequestURI()).thenReturn(uri);

        // and
        var httpResponse = mock(HttpServletResponse.class);

        // when
        Assertions.assertDoesNotThrow(() -> WebUtils.handleInvalidPath(mockedRequest, httpResponse, validPath));
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
}