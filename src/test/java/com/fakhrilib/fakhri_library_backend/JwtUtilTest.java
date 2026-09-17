package com.fakhrilib.fakhri_library_backend;

import com.fakhrilib.fakhri_library_backend.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "testSecretKeyThatIsLongEnoughForHMACSHA256SigningAlgorithm!");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtUtil.generateToken("admin");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_shouldBeValidAccessToken() {
        String token = jwtUtil.generateToken("admin");
        assertTrue(jwtUtil.isValid(token));
        assertTrue(jwtUtil.isAccessToken(token));
        assertFalse(jwtUtil.isRefreshToken(token));
    }

    @Test
    void generateRefreshToken_shouldBeValidRefreshToken() {
        String token = jwtUtil.generateRefreshToken("admin");
        assertTrue(jwtUtil.isValid(token));
        assertTrue(jwtUtil.isRefreshToken(token));
        assertFalse(jwtUtil.isAccessToken(token));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("admin");
        assertEquals("admin", jwtUtil.extractUsername(token));
    }

    @Test
    void isValid_shouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken("admin");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertFalse(jwtUtil.isValid(tampered));
    }

    @Test
    void isValid_shouldReturnFalseForGarbage() {
        assertFalse(jwtUtil.isValid(""));
        assertFalse(jwtUtil.isValid("not.a.token"));
    }

    @Test
    void accessToken_shouldNotBeAcceptedAsRefreshToken() {
        String accessToken = jwtUtil.generateToken("admin");
        assertFalse(jwtUtil.isRefreshToken(accessToken));
    }

    @Test
    void refreshToken_shouldNotBeAcceptedAsAccessToken() {
        String refreshToken = jwtUtil.generateRefreshToken("admin");
        assertFalse(jwtUtil.isAccessToken(refreshToken));
    }
}
