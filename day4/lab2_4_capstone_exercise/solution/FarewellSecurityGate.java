/**
 * Lab 2.4 (Capstone) - Farewell Endpoint Security Gate (solution)
 *
 * A scaled-down version of Lab 2.2's ApiSecurityGate, adapted to the new
 * /api/v1/farewells/{name} endpoint. Still a simulation: no real OAuth2/JWT,
 * no real Bucket4j limiter, and it does not need the shared quickstart API
 * running.
 *
 * Run: java FarewellSecurityGate.java
 */

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

    /** Authorization (RBAC) and rate limiting, combined into one small check. */
    static GateResult checkFarewellAccess(IncomingRequest r) {
        if (!REQUIRED_ROLE.equals(r.role())) {
            return new GateResult(403, "role '" + r.role() + "' does not match required role '" + REQUIRED_ROLE + "'");
        }
        if (r.requestsSoFarInWindow() > RATE_LIMIT_PER_WINDOW) {
            return new GateResult(429, "exceeded " + RATE_LIMIT_PER_WINDOW + " requests in this window");
        }
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
        System.out.println("\nF3 fails checkFarewellAccess() first (403, wrong role); F4 passes the role");
        System.out.println("check but fails the rate limit (429). F2 never reaches checkFarewellAccess()");
        System.out.println("at all — it is rejected by checkAuthentication() first, same as Lab 2.2's R2.");
    }
}
