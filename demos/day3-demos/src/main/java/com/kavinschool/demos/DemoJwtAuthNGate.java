/**
 * Demo - AuthN Gate: Who Are You, Really?
 * Day 3 - Session 2
 *
 * Goal: Show authentication as a gate a request must pass before anything
 * else runs. Uses a simplified "header.payload.signature"-shaped token
 * (not real JWT crypto) so the structure and failure modes are visible
 * without a crypto library.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day3/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoJwtAuthNGate
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

public class DemoJwtAuthNGate {

    // A simulated "now" so the demo is deterministic, not wall-clock dependent.
    static final long SIMULATED_NOW_EPOCH_SECONDS = 1_000_000;
    static final String EXPECTED_SIGNATURE = "trusted-signature";

    record IncomingToken(String label, String subject, long expiresAtEpochSeconds, String signature) {}

    record AuthNResult(boolean allowed, int status, String reason) {}

    static AuthNResult authenticate(IncomingToken token) {
        if (token == null) {
            return new AuthNResult(false, 401, "no token presented");
        }
        if (!EXPECTED_SIGNATURE.equals(token.signature())) {
            return new AuthNResult(false, 401, "signature does not match, token is forged or corrupted");
        }
        if (token.expiresAtEpochSeconds() <= SIMULATED_NOW_EPOCH_SECONDS) {
            return new AuthNResult(false, 401, "token expired at " + token.expiresAtEpochSeconds());
        }
        return new AuthNResult(true, 200, "authenticated as " + token.subject());
    }

    public static void main(String[] args) {
        IncomingToken[] requests = {
            new IncomingToken("valid token", "ada@example.com", SIMULATED_NOW_EPOCH_SECONDS + 900, EXPECTED_SIGNATURE),
            new IncomingToken("expired token", "grace@example.com", SIMULATED_NOW_EPOCH_SECONDS - 100, EXPECTED_SIGNATURE),
            new IncomingToken("tampered signature", "ada@example.com", SIMULATED_NOW_EPOCH_SECONDS + 900, "forged-signature"),
            null
        };

        System.out.println("Gating each request on authentication before anything else runs:\n");

        for (IncomingToken token : requests) {
            String label = token == null ? "missing token" : token.label();
            AuthNResult result = authenticate(token);
            System.out.printf("  %-20s -> %d %s%n", label, result.status(), result.reason());
        }

        System.out.println("\nTakeaway: authentication only answers \"who are you.\" A request that passes");
        System.out.println("this gate still has to clear authorization next, which decides what that");
        System.out.println("identity is actually allowed to do.");
    }
}
