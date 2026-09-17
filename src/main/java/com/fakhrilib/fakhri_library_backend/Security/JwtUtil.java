package com.fakhrilib.fakhri_library_backend.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // Refresh token lives 7 days
    private static final long REFRESH_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Access token — short lived (24h)
    public String generateToken(String username) {
        return buildToken(username, expirationMs, "access");
    }

    // Refresh token — long lived (7 days)
    public String generateRefreshToken(String username) {
        return buildToken(username, REFRESH_EXPIRATION_MS, "refresh");
    }

    private String buildToken(String username, long expMs, String type) {
        return Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expMs))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Only accept access tokens for API calls
    public boolean isAccessToken(String token) {
        try {
            String type = (String) getClaims(token).get("type");
            return "access".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    // Only accept refresh tokens for /auth/refresh
    public boolean isRefreshToken(String token) {
        try {
            String type = (String) getClaims(token).get("type");
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
