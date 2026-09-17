package com.fakhrilib.fakhri_library_backend;

import com.fakhrilib.fakhri_library_backend.Security.LoginRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    private LoginRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new LoginRateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "capacity", 3);
        ReflectionTestUtils.setField(rateLimiter, "refillMinutes", 1);
    }

    @Test
    void shouldAllowRequestsWithinLimit() {
        assertTrue(rateLimiter.isAllowed("192.168.1.1"));
        assertTrue(rateLimiter.isAllowed("192.168.1.1"));
        assertTrue(rateLimiter.isAllowed("192.168.1.1"));
    }

    @Test
    void shouldBlockRequestsExceedingLimit() {
        rateLimiter.isAllowed("10.0.0.1");
        rateLimiter.isAllowed("10.0.0.1");
        rateLimiter.isAllowed("10.0.0.1");
        // 4th attempt should be blocked
        assertFalse(rateLimiter.isAllowed("10.0.0.1"));
    }

    @Test
    void differentIpsShouldHaveSeparateBuckets() {
        // Exhaust IP1
        rateLimiter.isAllowed("1.1.1.1");
        rateLimiter.isAllowed("1.1.1.1");
        rateLimiter.isAllowed("1.1.1.1");
        assertFalse(rateLimiter.isAllowed("1.1.1.1"));

        // IP2 should still be allowed
        assertTrue(rateLimiter.isAllowed("2.2.2.2"));
    }
}
