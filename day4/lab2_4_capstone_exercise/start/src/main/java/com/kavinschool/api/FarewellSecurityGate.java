/**
 * Lab 2.4 (Capstone) - Farewell Endpoint Security Gate (starter)
 *
 * A scaled-down version of Lab 2.2's ApiSecurityGate, adapted to the new
 * /api/v1/farewells/{name} endpoint. Still a simulation: no real OAuth2/JWT,
 * no real Bucket4j limiter, and it does not need the shared quickstart API
 * running. The pattern is identical to Day 3 — only the endpoint it is
 * guarding has changed.
 *
 * Run: java FarewellSecurityGate.java
 */

package com.kavinschool.api;

import java.util.ArrayList;
import java.util.List;

public class FarewellSecurityGate {

    static final String EXPECTED_SIGNATURE = "trusted-signature";
    static final String REQUIRED_ROLE = "USER";
    static final int RATE_LIMIT_PER_WINDOW = 3;

    record IncomingRequest(
        String id,
        String signature,
        long expiresAtEpochSeconds,
        String role,
        int requestsSoFarInWindow
    ) {}

    record GateResult(int status, String reason) {}

    static final long SIMULATED_NOW_EPOCH_SECONDS = 1_000_000;

    static final List<IncomingRequest> REQUESTS = List.of(
        new IncomingRequest("F1", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", 1),
        new IncomingRequest("F2", "forged-signature", SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", 1),
        new IncomingRequest("F3", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "GUEST", 1),
        new IncomingRequest("F4", EXPECTED_SIGNATURE, SIMULATED_NOW_EPOCH_SECONDS + 900, "USER", 5)
    );

    /**
     * Worked example, carried over unchanged from Lab 2.2: authentication
     * must run, and fail, before anything else does.
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

    /**
     * TODO 1: Farewell access check (authorization + rate limit, combined).
     *
     * This endpoint is small enough that one check can cover both rules
     * from Lab 2.2, scaled down to a single method:
     *   - reject with 403 if r.role() does not equal REQUIRED_ROLE
     *   - otherwise reject with 429 if r.requestsSoFarInWindow() exceeds
     *     RATE_LIMIT_PER_WINDOW
     *   - otherwise return null (allowed)
     *
     * Check role before rate limit — the same order Lab 2.2 ran its two
     * separate checks in.
     */
    static GateResult checkFarewellAccess(IncomingRequest r) {
        // TODO 1: implement
        return null;
    }

    /** Runs every check in order, short-circuiting on the first failure. */
    static GateResult gate(IncomingRequest r) {
        GateResult authn = checkAuthentication(r);
        if (authn != null) return authn;

        GateResult access = checkFarewellAccess(r);
        if (access != null) return access;

        return new GateResult(200, "request served");
    }

    static void report() {
        System.out.println("=".repeat(88));
        System.out.println("FAREWELL ENDPOINT SECURITY GATE REPORT");
        System.out.println("=".repeat(88));

        List<GateResult> results = new ArrayList<>();
        for (IncomingRequest r : REQUESTS) {
            GateResult result = gate(r);
            results.add(result);
            System.out.printf("%n[%s] role=%-6s window=%d/%d%n",
                r.id(), r.role(), r.requestsSoFarInWindow(), RATE_LIMIT_PER_WINDOW);
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
        System.out.println("\nBefore you finish: F3 and F4 should both fail, but for different reasons.");
        System.out.println("Confirm checkFarewellAccess() rejects F3 with 403 and F4 with 429, and that");
        System.out.println("F2 never even reaches your new check.");
    }
}
