/**
 * Demo - Status Codes Carry the Outcome
 * Day 1 - Session 4
 *
 * Goal: Show what generic HTTP machinery does with correct status codes, and
 * what it does with an API that answers 200 OK for everything.
 *
 * The same twelve scenarios are run twice. First against a well-behaved API
 * that uses the right code, then against an "always 200" API that hides the
 * outcome in the body. A stock retry policy, cache, and error-rate dashboard
 * are then applied to both sets of responses. The dashboard on the second API
 * reports a perfect success rate while a third of the calls actually failed.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoStatusCodes
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoStatusCodes {

    record Scenario(String situation, int correctStatus, String reason) {}

    static final List<Scenario> SCENARIOS = List.of(
        new Scenario("GET an account that exists", 200, "plain success with a body"),
        new Scenario("POST a new account", 201, "created; include a Location header"),
        new Scenario("DELETE an account", 204, "success with nothing to return"),
        new Scenario("POST a long export job", 202, "accepted, not finished; give a status URL"),
        new Scenario("GET with a matching ETag", 304, "not modified; let the client use its cache"),
        new Scenario("Body is not valid JSON", 400, "the server could not parse the request"),
        new Scenario("No token supplied", 401, "unauthenticated: I do not know who you are"),
        new Scenario("Valid token, wrong role", 403, "authenticated but not permitted"),
        new Scenario("Account id does not exist", 404, "the resource is not there"),
        new Scenario("POST to a read-only collection", 405, "method not allowed; send Allow header"),
        new Scenario("Valid JSON, negative amount", 422, "parsed fine, failed business validation"),
        new Scenario("Caller exceeded its quota", 429, "too many requests; send Retry-After")
    );

    /**
     * Generic client behaviour, driven only by the status code.
     * Learners see that retry policies, caching rules, and error-rate
     * dashboards all key off the status line -- not the response body.
     */
    record ClientDecision(String retry, String cache, String countedAsError) {}

    /**
     * Maps a status code to the three decisions a generic HTTP client makes:
     * retry, cache, and error counting. Learners see that all three are
     * driven by the status line alone -- no body parsing needed.
     */
    static ClientDecision decide(int status) {
        String retry = switch (status / 100) {
            case 5 -> "retry with backoff";
            case 4 -> status == 429 ? "retry after Retry-After" : "do not retry";
            default -> "no retry needed";
        };
        String cache = switch (status) {
            case 200 -> "cacheable";
            case 304 -> "serve from cache";
            default -> "not cacheable";
        };
        String error = status >= 400 ? "yes" : "no";
        return new ClientDecision(retry, cache, error);
    }

    /**
     * Prints 12 scenarios with their correct status codes and the resulting
     * client decisions. Learners see that proper status codes give correct
     * retry, cache, and error-counting behaviour for free.
     */
    static void printCorrectApi() {
        System.out.println("=".repeat(96));
        System.out.println("API A: uses the status code the situation calls for");
        System.out.println("=".repeat(96));
        System.out.printf("%-34s %6s  %-26s %-24s %s%n",
            "SITUATION", "STATUS", "RETRY POLICY", "CACHE", "COUNTED AS ERROR");
        for (Scenario s : SCENARIOS) {
            ClientDecision d = decide(s.correctStatus());
            System.out.printf("%-34s %6d  %-26s %-24s %s%n",
                s.situation(), s.correctStatus(), d.retry(), d.cache(), d.countedAsError());
        }
    }

    /**
     * Runs the same 12 scenarios through an API that always returns 200 OK.
     * Learners see every failure disguised as success: no retries, no
     * backoff, everything cached, and the dashboard reports 0% errors.
     */
    static void printAlways200Api() {
        System.out.println("\n" + "=".repeat(96));
        System.out.println("API B: answers 200 OK and puts the outcome in the body");
        System.out.println("=".repeat(96));
        System.out.printf("%-34s %6s  %-26s %-24s %s%n",
            "SITUATION", "STATUS", "RETRY POLICY", "CACHE", "COUNTED AS ERROR");
        for (Scenario s : SCENARIOS) {
            ClientDecision d = decide(200);
            System.out.printf("%-34s %6d  %-26s %-24s %s%n",
                s.situation(), 200, d.retry(), d.cache(), d.countedAsError());
        }
    }

    /**
     * Compares the error-rate dashboard for both APIs. Learners see that
     * API A (correct codes) reports the real failure rate, while API B
     * (always 200) reports 0% despite a third of calls actually failing.
     */
    static void printDashboard() {
        long realFailures = SCENARIOS.stream().filter(s -> s.correctStatus() >= 400).count();
        int total = SCENARIOS.size();

        System.out.println("\n" + "=".repeat(96));
        System.out.println("WHAT THE ERROR-RATE DASHBOARD REPORTS");
        System.out.println("=".repeat(96));
        System.out.printf("  Calls that actually failed:     %d of %d%n", realFailures, total);
        System.out.printf("  API A reported error rate:      %.0f%%  (correct)%n",
            100.0 * realFailures / total);
        System.out.printf("  API B reported error rate:      %.0f%%  (every failure invisible)%n", 0.0);
        System.out.println();
        System.out.println("  API B also breaks: the 429 caller never learns to back off, the 401 caller");
        System.out.println("  never re-authenticates, the 304 saving is lost, and a 500 is never retried.");
    }

    /**
     * Prints the three status-code pairs most commonly confused in practice.
     * Learners memorize the distinction: 401 vs 403 (who vs. permission),
     * 400 vs 422 (parse vs. validate), and 200 vs 4xx (never hide failures).
     */
    static void printConfusedPairs() {
        System.out.println("\n" + "=".repeat(96));
        System.out.println("THE THREE PAIRS PEOPLE GET WRONG");
        System.out.println("=".repeat(96));
        System.out.println("  401 vs 403   401 = I do not know who you are.  403 = I know, and you may not.");
        System.out.println("  400 vs 422   400 = I could not parse it.       422 = I parsed it, it is invalid.");
        System.out.println("  200 vs 4xx   200 with {\"success\":false} defeats retries, caches, and alerts.");
    }

    /**
     * Orchestrates the demo: correct API, always-200 API, dashboard
     * comparison, and confused pairs. Learners walk away understanding
     * that the status code is the part of the response that machines act on.
     */
    public static void main(String[] args) {
        printCorrectApi();
        printAlways200Api();
        printDashboard();
        printConfusedPairs();

        System.out.println("\nTakeaway: the status code is the part of your response that machines act on.");
        System.out.println("Get it right and generic clients, caches, gateways, and dashboards work for free.");
        System.out.println("Answer 200 for everything and every consumer has to write custom handling for you.");
    }
}
