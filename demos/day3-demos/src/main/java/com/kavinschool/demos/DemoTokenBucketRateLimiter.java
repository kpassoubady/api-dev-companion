/**
 * Demo - Token Bucket Rate Limiting
 * Day 3 - Session 2
 *
 * Goal: Implement the token bucket algorithm from scratch to show how it
 * allows short bursts while still enforcing a steady average rate, and
 * what a 429 rejection looks like once the bucket is empty.
 *
 * Uses a manual simulated clock instead of System.currentTimeMillis(), so
 * the demo is deterministic and doesn't depend on real elapsed time.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day3/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoTokenBucketRateLimiter
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

public class DemoTokenBucketRateLimiter {

    static class TokenBucket {
        private final int capacity;
        private final double refillTokensPerSecond;
        private double tokens;
        private long lastRefillMillis;

        TokenBucket(int capacity, double refillTokensPerSecond, long startMillis) {
            this.capacity = capacity;
            this.refillTokensPerSecond = refillTokensPerSecond;
            this.tokens = capacity;
            this.lastRefillMillis = startMillis;
        }

        void refillTo(long nowMillis) {
            double elapsedSeconds = (nowMillis - lastRefillMillis) / 1000.0;
            tokens = Math.min(capacity, tokens + elapsedSeconds * refillTokensPerSecond);
            lastRefillMillis = nowMillis;
        }

        boolean tryConsume(long nowMillis) {
            refillTo(nowMillis);
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        long simulatedNow = 0;
        // Capacity 3 (allows a burst of 3), refilling at 1 token/second.
        TokenBucket bucket = new TokenBucket(3, 1.0, simulatedNow);

        System.out.println("Bucket capacity 3, refill rate 1 token/second.\n");
        System.out.println("A client fires 5 requests in the same instant (a burst):");
        for (int i = 1; i <= 5; i++) {
            boolean served = bucket.tryConsume(simulatedNow);
            System.out.printf("  request %d at t=%dms -> %s%n", i, simulatedNow, served ? "200 served" : "429 rejected");
        }

        System.out.println("\nClient waits 2 simulated seconds, then tries again:");
        simulatedNow += 2000;
        for (int i = 6; i <= 8; i++) {
            boolean served = bucket.tryConsume(simulatedNow);
            System.out.printf("  request %d at t=%dms -> %s%n", i, simulatedNow, served ? "200 served" : "429 rejected");
        }

        System.out.println("\nTakeaway: the bucket allowed an initial burst of 3, rejected the overflow,");
        System.out.println("then let 2 more through once tokens refilled. That's the tradeoff rate limiting");
        System.out.println("makes on purpose: absorb bursts, but never exceed the long-term average rate.");
    }
}
