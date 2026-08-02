/**
 * Lab 2.2 - API Security Gate (solution)
 *
 * Combines authentication, authorization, and rate limiting into one gate a
 * request must clear in order. Simulated tokens and a simulated clock stand
 * in for real OAuth2/JWT and Bucket4j.
 *
 * Run: java ApiSecurityGate.java
 */

package com.kavinschool.api;

import java.util.ArrayList;
import java.util.List;

public class ApiSecurityGate {

    static final String EXPECTED_SIGNATURE = "trusted-signature";
    static final int RATE_LIMIT_PER_WINDOW = 3;

    record IncomingRequest(
        String id,
        String signature,
        long expiresAtEpochSeconds,
        String role,
        String requiredRole,
        int requestsSoFarInWindow
    ) {}

    record GateResult(int status, String reason) {}

    static final long SIMULATED_NOW_EPOCH_SECONDS = 1_000_000;

    static final List<IncomingRequest> REQUESTS = List.of(
        new IncomingRequest("R1", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "ADMIN", "ADMIN", 1),
        new IncomingRequest("R2", "forged-signature", SIMULATED_NOW_EPOCH_SECONDS + 900, "ADMIN", "ADMIN", 1),
        new IncomingRequest("R3", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS - 100, "ADMIN", "ADMIN", 1),
        new IncomingRequest("R4", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", "ADMIN", 1),
        new IncomingRequest("R5", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "ADMIN", "ADMIN", 4),
        new IncomingRequest("R6", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", "USER", 2),
        new IncomingRequest("R7", "forged-signature", SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", "ADMIN", 5)
    );

    /**
     * Authentication fails on a missing/invalid signature or an expired
     * token. This must run, and fail, before anything else does.
     */
    static GateResult checkAuthentication(IncomingRequest r) {
        if (!EXPECTED_SIGNATURE.equals(r.signature())) {
            return new GateResult(401, "signature does not match, token is forged or corrupted");
        }
        if (r.expiresAtEpochSeconds() <= SIMULATED_NOW_EPOCH_SECONDS) {
            return new GateResult(401, "token expired at " + r.expiresAtEpochSeconds());
        }
        return null;
    }

    /** Authorization (RBAC): the caller's role must match what this endpoint requires. */
    static GateResult checkAuthorization(IncomingRequest r) {
        if (!r.role().equals(r.requiredRole())) {
            return new GateResult(403, "role '" + r.role() + "' does not match required role '" + r.requiredRole() + "'");
        }
        return null;
    }

    /** Rate limiting: reject once the caller exceeds the allowed requests for this window. */
    static GateResult checkRateLimit(IncomingRequest r) {
        if (r.requestsSoFarInWindow() > RATE_LIMIT_PER_WINDOW) {
            return new GateResult(429, "exceeded " + RATE_LIMIT_PER_WINDOW + " requests in this window");
        }
        return null;
    }

    /** Runs every check in order, short-circuiting on the first failure. */
    static GateResult gate(IncomingRequest r) {
        GateResult authn = checkAuthentication(r);
        if (authn != null) return authn;

        GateResult authz = checkAuthorization(r);
        if (authz != null) return authz;

        GateResult rateLimit = checkRateLimit(r);
        if (rateLimit != null) return rateLimit;

        return new GateResult(200, "request served");
    }

    static void report() {
        System.out.println("=".repeat(88));
        System.out.println("API SECURITY GATE REPORT");
        System.out.println("=".repeat(88));

        List<GateResult> results = new ArrayList<>();
        for (IncomingRequest r : REQUESTS) {
            GateResult result = gate(r);
            results.add(result);
            System.out.printf("%n[%s] role=%-6s required=%-6s window=%d/%d%n",
                r.id(), r.role(), r.requiredRole(), r.requestsSoFarInWindow(), RATE_LIMIT_PER_WINDOW);
            System.out.printf("       -> %d %s%n", result.status(), result.reason());
        }

        long served = results.stream().filter(res -> res.status() == 200).count();
        System.out.println("\n" + "-".repeat(88));
        System.out.printf("Requests evaluated : %d%n", REQUESTS.size());
        System.out.printf("Served (200)       : %d%n", served);
        System.out.printf("Rejected           : %d%n", REQUESTS.size() - served);
    }

    public static void main(String[] args) {
        report();
        System.out.println("\nBefore you finish: notice which check stopped each rejected request. A");
        System.out.println("request only reaches the rate limit check if it already passed authentication");
        System.out.println("and authorization, exactly the order production Spring Security filters run in.");
    }
}
