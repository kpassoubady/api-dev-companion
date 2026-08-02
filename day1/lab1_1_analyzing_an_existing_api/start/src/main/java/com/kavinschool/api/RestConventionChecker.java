/**
 * Lab 1.1 - REST Convention Checker (starter)
 *
 * Scores observed API endpoints against the eight resource-naming rules, the
 * method guarantees, and the RFC 9457 error-body standard from Day 1 Session 4.
 *
 * Rule 1 is implemented for you as a worked example. Implement the four TODOs,
 * then add your own observations from Postman.
 *
 * Run: java RestConventionChecker.java
 */

package com.kavinschool.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestConventionChecker {

    /**
     * One endpoint as actually observed in Postman or curl. The rows below are
     * the quickstart API. TODO 5 asks you to add more.
     */
    record Observation(
        String method,
        String path,
        int observedStatus,
        String observedContentType,
        String note
    ) {}

    record Finding(String rule, String severity, String message) {}

    static final List<Observation> OBSERVATIONS = List.of(
        new Observation("GET", "/api/v1/health", 200, "application/json",
            "returns {\"status\":\"UP\",\"service\":\"api-dev-quickstart\"}"),
        new Observation("GET", "/api/v1/greetings/Ada", 200, "application/json",
            "reflects the path variable into the message"),
        new Observation("POST", "/api/v1/health", 405, "application/json",
            "Allow: GET is present, which is correct"),
        new Observation("GET", "/api/v1/accounts/999", 404, "application/json",
            "Spring default error body with a trace field")

        // TODO 5: add the endpoints you observed yourself.
        //   Send each request in Postman, then record the real method, path,
        //   status, Content-Type, and anything notable about the body.
        //   Start with /api-docs and /swagger-ui.html, then add any endpoint
        //   from the second API you chose for the stretch goal.
    );

    static final List<String> VERB_PREFIXES = List.of(
        "get", "create", "update", "delete", "fetch", "list", "do", "make", "set", "cancel"
    );

    /** Words that are legitimately singular even as a path segment. */
    static final List<String> UNCOUNTABLE = List.of("health", "status", "search", "me");

    /**
     * Rule 1, implemented for you as the worked example. The verb belongs in
     * the HTTP method, so a path segment starting with an action word is a
     * violation. Read this carefully: the four TODOs follow the same shape.
     */
    static List<Finding> checkNoVerbsInPath(Observation o) {
        List<Finding> findings = new ArrayList<>();
        for (String segment : segmentsOf(o.path())) {
            String lower = segment.toLowerCase(Locale.ROOT);
            for (String verb : VERB_PREFIXES) {
                if (lower.startsWith(verb)) {
                    findings.add(new Finding("rule 1", "HIGH",
                        "path segment '" + segment + "' starts with the verb '" + verb
                        + "'; the HTTP method is the verb"));
                    break;
                }
            }
        }
        return findings;
    }

    /**
     * TODO 1: Rule 2, collections are plural.
     *
     * For each segment of the path:
     *   - skip it if isIdentifier(segment) or isInfrastructure(segment)
     *   - skip it if UNCOUNTABLE contains the lowercased segment
     *   - skip it if the lowercased segment already ends in "s"
     *   - otherwise add a MEDIUM finding for "rule 2" naming the segment
     *
     * Expected on the starter data: no rule 2 findings, because the quickstart
     * API gets this right. Prove that rather than assuming it: temporarily add
     * an Observation for "/api/v1/order/42" and confirm your check fires.
     */
    static List<Finding> checkPluralCollections(Observation o) {
        List<Finding> findings = new ArrayList<>();
        // TODO 1: implement
        return findings;
    }

    /**
     * TODO 2: Rules 4 and 6, naming style.
     *
     * Rule 4: a segment you named must be lowercase-with-hyphens. Report a
     * MEDIUM finding for an uppercase character and one for an underscore.
     * Check segment by segment and skip isIdentifier(segment) segments, because
     * /greetings/Ada is data, not a naming decision. Getting this wrong is the
     * most common mistake in this lab.
     *
     * Rule 6: report a LOW finding when the path ends in .json, .xml, or .html,
     * and a LOW finding when the path has a trailing slash.
     */
    static List<Finding> checkNamingStyle(Observation o) {
        List<Finding> findings = new ArrayList<>();
        // TODO 2: implement
        return findings;
    }

    /**
     * TODO 3: Rule 7, explicit versioning.
     *
     * A path is versioned when it matches "^/api/v\\d+(/.*)?$". If it does not,
     * add a HIGH finding for "rule 7" explaining that a caller cannot tell
     * which contract it is bound to.
     *
     * Two endpoints in the finished data will fail this check and both are
     * arguably fine. Note in your worksheet why.
     */
    static List<Finding> checkVersioning(Observation o) {
        List<Finding> findings = new ArrayList<>();
        // TODO 3: implement
        return findings;
    }

    /**
     * TODO 4: the RFC 9457 error-body standard.
     *
     * When observedStatus() is 400 or above:
     *   - if observedContentType() does not start with "application/problem+json",
     *     add a MEDIUM finding for "RFC 9457"
     *   - if the note mentions a trace, add a HIGH finding for "security",
     *     because a stack trace in an error body leaks internals to callers
     *
     * The second check finds a real defect in the quickstart API.
     */
    static List<Finding> checkErrorResponseShape(Observation o) {
        List<Finding> findings = new ArrayList<>();
        // TODO 4: implement
        return findings;
    }

    /** Rule 5, given: the path identifies, the query filters and paginates. */
    static List<Finding> checkPathIdentifies(Observation o) {
        List<Finding> findings = new ArrayList<>();
        if (o.path().matches(".*[?&]id=.*")) {
            findings.add(new Finding("rule 5", "HIGH",
                "identity is in the query string; move it into the path"));
        }
        return findings;
    }

    /** Method and status agreement, given. */
    static List<Finding> checkMethodStatusAgreement(Observation o) {
        List<Finding> findings = new ArrayList<>();
        int status = o.observedStatus();
        if (o.method().equals("GET") && (status == 201 || status == 204)) {
            findings.add(new Finding("methods", "MEDIUM",
                "GET answered " + status + "; a read should answer 200 or 304"));
        }
        if (o.method().equals("POST") && status == 200 && !o.path().contains("health")) {
            findings.add(new Finding("methods", "LOW",
                "POST answered 200; a creation should answer 201 with a Location header"));
        }
        return findings;
    }

    static List<Finding> checkAll(Observation o) {
        List<Finding> all = new ArrayList<>();
        all.addAll(checkNoVerbsInPath(o));
        all.addAll(checkPluralCollections(o));
        all.addAll(checkNamingStyle(o));
        all.addAll(checkVersioning(o));
        all.addAll(checkPathIdentifies(o));
        all.addAll(checkErrorResponseShape(o));
        all.addAll(checkMethodStatusAgreement(o));
        return all;
    }

    // ---- helpers, all given ----

    static List<String> segmentsOf(String path) {
        List<String> segments = new ArrayList<>();
        for (String raw : path.split("\\?")[0].split("/")) {
            if (!raw.isBlank()) {
                segments.add(raw);
            }
        }
        return segments;
    }

    /** A numeric id, a {template} placeholder, or a capitalised value. */
    static boolean isIdentifier(String segment) {
        return segment.matches("\\d+(\\.\\w+)?") || segment.matches("\\{.+\\}")
            || segment.matches("[A-Z][a-z]+");
    }

    /** Framework or tooling segments that are not resources you named. */
    static boolean isInfrastructure(String segment) {
        String lower = segment.toLowerCase(Locale.ROOT);
        return lower.equals("api") || lower.matches("v\\d+")
            || lower.equals("api-docs") || lower.startsWith("swagger-ui");
    }

    static void report() {
        int totalFindings = 0;
        int cleanEndpoints = 0;

        System.out.println("=".repeat(84));
        System.out.println("REST CONVENTION REPORT");
        System.out.println("=".repeat(84));

        for (Observation o : OBSERVATIONS) {
            List<Finding> findings = checkAll(o);
            totalFindings += findings.size();
            if (findings.isEmpty()) {
                cleanEndpoints++;
            }
            System.out.printf("%n%-6s %-28s -> %d %s%n",
                o.method(), o.path(), o.observedStatus(), o.observedContentType());
            if (findings.isEmpty()) {
                System.out.println("       PASS  no findings");
            } else {
                for (Finding f : findings) {
                    System.out.printf("       %-6s %-10s %s%n", f.severity(), f.rule(), f.message());
                }
            }
        }

        System.out.println("\n" + "-".repeat(84));
        System.out.printf("Endpoints observed : %d%n", OBSERVATIONS.size());
        System.out.printf("Endpoints clean    : %d%n", cleanEndpoints);
        System.out.printf("Total findings     : %d%n", totalFindings);
        System.out.printf("Adherence score    : %.0f%%%n",
            100.0 * cleanEndpoints / OBSERVATIONS.size());
    }

    public static void main(String[] args) {
        report();
        System.out.println("\nBefore you finish: a finding is not automatically a defect. Decide for each");
        System.out.println("one whether the deviation is defensible, and write your reasoning in the");
        System.out.println("worksheet. That judgement is the point of this lab, not the score.");
    }
}
