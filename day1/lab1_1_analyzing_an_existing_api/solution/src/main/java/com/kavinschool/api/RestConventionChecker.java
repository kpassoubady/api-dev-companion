/**
 * Lab 1.1 - REST Convention Checker (reference solution)
 *
 * Scores observed API endpoints against the eight resource-naming rules, the
 * method guarantees, and the RFC 9457 error-body standard from Day 1 Session 4.
 *
 * Run: java RestConventionChecker.java
 */

package com.kavinschool.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestConventionChecker {

    /**
     * One endpoint as actually observed in Postman or curl. Fill these in from
     * the API you are analysing. The rows below are the quickstart API.
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
            "Spring default error body with a trace field"),
        new Observation("GET", "/api-docs", 200, "application/json",
            "springdoc tooling endpoint; declares info.version v0 while paths say v1"),
        new Observation("GET", "/swagger-ui.html", 302, "(none)",
            "302 redirect to /swagger-ui/index.html; springdoc tooling endpoint")
    );

    static final List<String> VERB_PREFIXES = List.of(
        "get", "create", "update", "delete", "fetch", "list", "do", "make", "set", "cancel"
    );

    static final List<String> UNCOUNTABLE = List.of("health", "status", "search", "me");

    /**
     * Rule 1, given as the worked example. The verb belongs in the HTTP method,
     * so a path segment that starts with an action word is a violation.
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

    /** Rule 2: collections are plural. */
    static List<Finding> checkPluralCollections(Observation o) {
        List<Finding> findings = new ArrayList<>();
        for (String segment : segmentsOf(o.path())) {
            if (isIdentifier(segment) || isInfrastructure(segment)) {
                continue;
            }
            String lower = segment.toLowerCase(Locale.ROOT);
            if (UNCOUNTABLE.contains(lower) || lower.endsWith("s")) {
                continue;
            }
            findings.add(new Finding("rule 2", "MEDIUM",
                "collection segment '" + segment + "' is singular; use the plural form"));
        }
        return findings;
    }

    /** Rules 4 and 6: lowercase-with-hyphens, no extensions, no trailing slash. */
    static List<Finding> checkNamingStyle(Observation o) {
        List<Finding> findings = new ArrayList<>();
        String path = o.path();

        // Only the segments you named count. A path-variable value such as
        // /greetings/Ada is data, not a naming decision.
        for (String segment : segmentsOf(path)) {
            if (isIdentifier(segment)) {
                continue;
            }
            if (segment.matches(".*[A-Z].*")) {
                findings.add(new Finding("rule 4", "MEDIUM",
                    "segment '" + segment + "' is camelCase; use lowercase-with-hyphens"));
            }
            if (segment.contains("_")) {
                findings.add(new Finding("rule 4", "MEDIUM",
                    "segment '" + segment + "' uses an underscore; use hyphens between words"));
            }
        }
        if (path.matches(".*\\.(json|xml|html)$")) {
            findings.add(new Finding("rule 6", "LOW",
                "path ends in a file extension; content negotiation belongs in the Accept header"));
        }
        if (path.length() > 1 && path.endsWith("/")) {
            findings.add(new Finding("rule 6", "LOW", "path has a trailing slash"));
        }
        return findings;
    }

    /** Rule 7: the version is an explicit path segment. */
    static List<Finding> checkVersioning(Observation o) {
        List<Finding> findings = new ArrayList<>();
        boolean versioned = o.path().matches("^/api/v\\d+(/.*)?$");
        if (!versioned) {
            findings.add(new Finding("rule 7", "HIGH",
                "no version segment; a caller cannot tell which contract it is bound to"));
        }
        return findings;
    }

    /** Rule 5: the path identifies, the query filters and paginates. */
    static List<Finding> checkPathIdentifies(Observation o) {
        List<Finding> findings = new ArrayList<>();
        if (o.path().matches(".*[?&]id=.*")) {
            findings.add(new Finding("rule 5", "HIGH",
                "identity is in the query string; move it into the path"));
        }
        return findings;
    }

    /**
     * Error-body standard: a 4xx or 5xx should carry application/problem+json
     * per RFC 9457 rather than a hand-rolled shape.
     */
    static List<Finding> checkErrorResponseShape(Observation o) {
        List<Finding> findings = new ArrayList<>();
        if (o.observedStatus() >= 400) {
            if (!o.observedContentType().startsWith("application/problem+json")) {
                findings.add(new Finding("RFC 9457", "MEDIUM",
                    "error response is " + o.observedContentType()
                    + "; RFC 9457 expects application/problem+json"));
            }
            if (o.note().toLowerCase(Locale.ROOT).contains("trace")) {
                findings.add(new Finding("security", "HIGH",
                    "error body includes a stack trace; this leaks internals to callers"));
            }
        }
        return findings;
    }

    /** Method and status agreement, drawn from the Session 4 table. */
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

    static List<String> segmentsOf(String path) {
        List<String> segments = new ArrayList<>();
        for (String raw : path.split("\\?")[0].split("/")) {
            if (!raw.isBlank()) {
                segments.add(raw);
            }
        }
        return segments;
    }

    static boolean isIdentifier(String segment) {
        return segment.matches("\\d+(\\.\\w+)?") || segment.matches("\\{.+\\}")
            || segment.matches("[A-Z][a-z]+");
    }

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
        System.out.println("\nRead the findings against your worksheet. A finding is not automatically a");
        System.out.println("defect: /api-docs and /swagger-ui.html are springdoc tooling endpoints, not part");
        System.out.println("of the API's own contract, so their rule 7 findings are defensible. The stack");
        System.out.println("trace in the 404 body is not defensible.");
    }
}
