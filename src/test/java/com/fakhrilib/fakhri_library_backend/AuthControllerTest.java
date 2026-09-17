package com.fakhrilib.fakhri_library_backend;

import com.fakhrilib.fakhri_library_backend.Controller.AuthController;
import com.fakhrilib.fakhri_library_backend.Security.JwtUtil;
import com.fakhrilib.fakhri_library_backend.Security.LoginRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private AuthController authController;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "testSecretKeyThatIsLongEnoughForHMACSHA256SigningAlgorithm!");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);

        LoginRateLimiter rateLimiter = new LoginRateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "capacity", 100);
        ReflectionTestUtils.setField(rateLimiter, "refillMinutes", 1);

        authController = new AuthController(jwtUtil, rateLimiter);
        ReflectionTestUtils.setField(authController, "adminUsername", "admin");
        ReflectionTestUtils.setField(authController, "adminPassword", "Fakhri@Library2026!");
    }

    @Test
    void login_withCorrectCredentials_shouldReturn200WithTokens() {
        var req = new AuthController.LoginRequest("admin", "Fakhri@Library2026!");
        var response = authController.login(req, new MockHttpServletRequest());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("token"));
        assertTrue(response.getBody().containsKey("refreshToken"));
    }

    @Test
    void login_withWrongPassword_shouldReturn401() {
        var req = new AuthController.LoginRequest("admin", "wrongpassword");
        var response = authController.login(req, new MockHttpServletRequest());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_withWrongUsername_shouldReturn401() {
        var req = new AuthController.LoginRequest("hacker", "Fakhri@Library2026!");
        var response = authController.login(req, new MockHttpServletRequest());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_errorMessage_shouldBeGeneric() {
        var req = new AuthController.LoginRequest("wrong", "wrongpass");
        var response = authController.login(req, new MockHttpServletRequest());
        String error = (String) response.getBody().get("error");
        assertEquals("Invalid credentials.", error);
    }

    @Test
    void refresh_withValidRefreshToken_shouldReturnNewTokens() {
        var loginReq = new AuthController.LoginRequest("admin", "Fakhri@Library2026!");
        var loginRes = authController.login(loginReq, new MockHttpServletRequest());
        String refreshToken = (String) loginRes.getBody().get("refreshToken");
        var response = authController.refresh("Bearer " + refreshToken);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("token"));
        assertTrue(response.getBody().containsKey("refreshToken"));
    }

    @Test
    void refresh_withAccessToken_shouldReturn401() {
        var loginReq = new AuthController.LoginRequest("admin", "Fakhri@Library2026!");
        var loginRes = authController.login(loginReq, new MockHttpServletRequest());
        String accessToken = (String) loginRes.getBody().get("token");
        var response = authController.refresh("Bearer " + accessToken);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void verify_withValidAccessToken_shouldReturn200() {
        var loginReq = new AuthController.LoginRequest("admin", "Fakhri@Library2026!");
        var loginRes = authController.login(loginReq, new MockHttpServletRequest());
        String token = (String) loginRes.getBody().get("token");
        var response = authController.verify("Bearer " + token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("valid"));
    }

    @Test
    void verify_withInvalidToken_shouldReturn401() {
        var response = authController.verify("Bearer invalidtoken");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
