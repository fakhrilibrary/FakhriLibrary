package com.fakhrilib.fakhri_library_backend.Security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    @Value("${login.rate-limit.capacity:5}")
    private int capacity;

    @Value("${login.rate-limit.refill-minutes:1}")
    private int refillMinutes;

    // One bucket per IP address
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofMinutes(refillMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean isAllowed(String ip) {
        Bucket bucket = buckets.computeIfAbsent(ip, k -> newBucket());
        return bucket.tryConsume(1);
    }
}
