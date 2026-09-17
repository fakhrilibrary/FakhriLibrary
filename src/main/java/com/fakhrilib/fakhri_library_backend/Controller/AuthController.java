package com.fakhrilib.fakhri_library_backend.Controller;

import com.fakhrilib.fakhri_library_backend.Security.JwtUtil;
import com.fakhrilib.fakhri_library_backend.Security.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final LoginRateLimiter rateLimiter;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AuthController(JwtUtil jwtUtil, LoginRateLimiter rateLimiter) {
        this.jwtUtil = jwtUtil;
        this.rateLimiter = rateLimiter;
    }

    public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 50, message = "Username too long")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be 6–100 characters")
        String password
    ) {}

    // ── LOGIN — returns both access + refresh tokens
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq) {

        String ip = getClientIp(httpReq);
        if (!rateLimiter.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many requests. Please try again later."));
        }

        boolean usernameMatch = constantTimeEquals(req.username(), adminUsername);
        boolean passwordMatch = constantTimeEquals(req.password(), adminPassword);

        if (!usernameMatch || !passwordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials."));
        }

        String accessToken  = jwtUtil.generateToken(req.username());
        String refreshToken = jwtUtil.generateRefreshToken(req.username());

        return ResponseEntity.ok(Map.of(
                "token",        accessToken,
                "refreshToken", refreshToken,
                "name",         "Administrator",
                "expiresIn",    86400,
                "refreshExpiresIn", 604800
        ));
    }

    // ── REFRESH — accepts refresh token, issues new access token
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token required."));
        }

        String refreshToken = authHeader.substring(7);

        if (!jwtUtil.isValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token."));
        }

        String username    = jwtUtil.extractUsername(refreshToken);
        String newAccess   = jwtUtil.generateToken(username);
        String newRefresh  = jwtUtil.generateRefreshToken(username); // rotate refresh token

        return ResponseEntity.ok(Map.of(
                "token",        newAccess,
                "refreshToken", newRefresh,
                "expiresIn",    86400
        ));
    }

    // ── VERIFY — checks access token validity
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No token."));
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.isValid(token) || !jwtUtil.isAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token invalid or expired."));
        }
        return ResponseEntity.ok(Map.of(
                "valid",    true,
                "username", jwtUtil.extractUsername(token)
        ));
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) {
            int dummy = 0;
            for (int i = 0; i < a.length(); i++) dummy ^= a.charAt(i);
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
