/**
 * Demo - Scoring Endpoints Against REST Naming Rules
 * Day 1 - Session 4
 *
 * Goal: Run a set of real-looking endpoints through the eight resource-naming
 * rules, print the violations, and print the RESTful rewrite for each.
 *
 * This demo is deliberately the tool that Lab 1.1 asks students to build for
 * themselves. Watching it run first makes the lab a matter of implementing
 * checks they have already seen the output of.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoResourceNaming
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DemoResourceNaming {

    record Endpoint(String method, String path, String suggestedMethod, String suggestedPath) {}

    static final List<Endpoint> ENDPOINTS = List.of(
        new Endpoint("GET", "/api/v1/orders", "GET", "/api/v1/orders"),
        new Endpoint("GET", "/api/v1/getOrderById?id=42", "GET", "/api/v1/orders/42"),
        new Endpoint("POST", "/api/v1/createOrder", "POST", "/api/v1/orders"),
        new Endpoint("GET", "/api/v1/order/42", "GET", "/api/v1/orders/42"),
        new Endpoint("GET", "/api/v1/customers/17/orders", "GET", "/api/v1/customers/17/orders"),
        new Endpoint("GET", "/api/v1/orders/shipped", "GET", "/api/v1/orders?status=shipped"),
        new Endpoint("GET", "/api/v1/purchaseOrders", "GET", "/api/v1/purchase-orders"),
        new Endpoint("GET", "/api/v1/purchase_orders", "GET", "/api/v1/purchase-orders"),
        new Endpoint("GET", "/api/v1/orders/42.json", "GET", "/api/v1/orders/42"),
        new Endpoint("GET", "/api/v1/orders/", "GET", "/api/v1/orders"),
        new Endpoint("GET", "/orders/42", "GET", "/api/v1/orders/42"),
        new Endpoint("GET", "/api/v1/tbl_cust_master/17", "GET", "/api/v1/customers/17"),
        new Endpoint("GET", "/api/v1/deleteOrder?id=42", "DELETE", "/api/v1/orders/42"),
        new Endpoint("POST", "/api/v1/orders/42/cancel", "POST", "/api/v1/orders/42/cancellation"),
        new Endpoint("GET", "/api/v1/orders?customerid=17", "GET", "/api/v1/customers/17/orders")
    );

    static final List<String> VERB_PREFIXES = List.of(
        "get", "create", "update", "delete", "fetch", "list", "do", "make", "set", "cancel"
    );

    static final List<String> KNOWN_COLLECTIONS = List.of(
        "orders", "customers", "accounts", "purchase-orders", "cancellation", "transfers"
    );

    static final List<String> STATUS_WORDS = List.of(
        "shipped", "pending", "cancelled", "active", "suspended"
    );

    /**
     * Scores a single endpoint against the eight REST naming rules and
     * returns a list of violation descriptions. Learners see the concrete
     * checks behind each rule -- this is the engine that Lab 1.1 asks them
     * to build for themselves.
     */
    static List<String> violations(Endpoint e) {
        List<String> found = new ArrayList<>();
        String path = e.path();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isBlank()) {
                continue;
            }
            String base = segment.split("\\?")[0].toLowerCase(Locale.ROOT);
            for (String verb : VERB_PREFIXES) {
                if (base.startsWith(verb)) {
                    found.add("rule 1: verb '" + verb + "' in the path; the HTTP method is the verb");
                    break;
                }
            }
        }

        for (String segment : segments) {
            String base = segment.split("\\?")[0];
            // Skip ids ("42", "42.json"), the api prefix, and version segments.
            if (base.isBlank() || base.matches("\\d+(\\.\\w+)?") || base.equals("api")
                || base.matches("v\\d+")) {
                continue;
            }
            String lower = base.toLowerCase(Locale.ROOT);
            boolean verbish = VERB_PREFIXES.stream().anyMatch(lower::startsWith);
            boolean schemaName = lower.startsWith("tbl_") || lower.startsWith("tab_")
                || lower.contains("_master");
            // A status used as a segment is reported by rule 5, so do not also
            // report it as a singular collection.
            boolean statusWord = STATUS_WORDS.contains(lower);
            if (!verbish && !schemaName && !statusWord
                && !lower.endsWith("s") && !KNOWN_COLLECTIONS.contains(lower)) {
                found.add("rule 2: '" + base + "' is singular; collections are plural");
            }
        }

        if (path.matches(".*[A-Z].*")) {
            found.add("rule 4: camelCase segment; use lowercase-with-hyphens");
        }
        if (path.contains("_")) {
            found.add("rule 4: underscore in a segment; use hyphens");
        }
        if (path.contains("?id=") || path.contains("&id=")) {
            found.add("rule 5: identity in the query string; the path identifies, the query filters");
        }
        if (path.toLowerCase(Locale.ROOT).matches(".*[?&][a-z]+id=.*")) {
            found.add("rule 3: a parent id in the query string; nest it as /parent/{id}/child instead");
        }
        if (path.matches(".*/(shipped|pending|cancelled|active)(/|$).*")) {
            found.add("rule 5: a status as a path segment; make it ?status=... instead");
        }
        if (path.matches(".*\\.(json|xml)$")) {
            found.add("rule 6: file extension; content negotiation belongs in the Accept header");
        }
        if (path.length() > 1 && path.endsWith("/")) {
            found.add("rule 6: trailing slash");
        }
        if (!path.matches("^/api/v\\d+/.*")) {
            found.add("rule 7: no version segment; version explicitly");
        }
        if (path.matches(".*/(tbl_|tab_)[a-z_]+.*") || path.contains("_master")) {
            found.add("rule 8: leaks the database schema; a refactor becomes a breaking change");
        }
        return found;
    }

    /**
     * Runs all 15 endpoints through the violation checker and prints a
     * PASS/FAIL report with suggested rewrites. Learners see the concrete
     * before-and-after for each anti-pattern: verb-in-path, singular
     * collection, camelCase, query-id, status-as-segment, and schema leak.
     */
    static void printReport() {
        int clean = 0;
        for (Endpoint e : ENDPOINTS) {
            List<String> issues = violations(e);
            String verdict = issues.isEmpty() ? "PASS" : issues.size() + " issue(s)";
            System.out.printf("%n%-7s %-34s  %s%n", e.method(), e.path(), verdict);
            if (issues.isEmpty()) {
                clean++;
            } else {
                issues.forEach(i -> System.out.println("        - " + i));
                System.out.printf("        => %s %s%n", e.suggestedMethod(), e.suggestedPath());
            }
        }
        System.out.printf("%n%d of %d endpoints are already conventional.%n", clean, ENDPOINTS.size());
    }

    /**
     * Prints the eight REST resource-naming rules in order of diagnostic
     * frequency. Learners get a quick-reference cheat sheet to use during
     * the breakout lab and on their own projects.
     */
    static void printRules() {
        System.out.println("THE EIGHT RULES, ORDERED BY HOW MANY DEFECTS EACH ONE CATCHES");
        System.out.println("  1. Nouns, not verbs                    /orders, never /getOrders");
        System.out.println("  2. Plural collections                  /accounts/42, not /account/42");
        System.out.println("  3. Hierarchy means containment         /customers/17/orders");
        System.out.println("  4. lowercase-with-hyphens              /purchase-orders");
        System.out.println("  5. Path identifies, query filters      /orders?status=shipped&page=2");
        System.out.println("  6. No extensions, no trailing slash    Accept header does negotiation");
        System.out.println("  7. Version explicitly                  /api/v1/orders");
        System.out.println("  8. Do not leak the schema              not /tbl_cust_master");
    }

    /**
     * Orchestrates the demo: prints the rules, scores 15 endpoints, and
     * addresses the deliberate exception (/cancel as a verb). Learners
     * walk away understanding that the rules are not aesthetics -- each one
     * exists so a caller can guess the next endpoint without documentation.
     */
    public static void main(String[] args) {
        printRules();
        System.out.println("\n" + "=".repeat(72));
        System.out.println("SCORING 15 ENDPOINTS");
        System.out.println("=".repeat(72));
        printReport();

        System.out.println("\nNote on the last one: /orders/42/cancel is a verb, and it is the case where");
        System.out.println("strict REST is genuinely awkward. Modelling the action as a resource");
        System.out.println("(/orders/42/cancellation) keeps the convention; many teams accept the verb");
        System.out.println("instead and document it. Either is defensible. Silence is not.");

        System.out.println("\nTakeaway: the rules are not aesthetics. Each one exists so a caller can guess");
        System.out.println("your next endpoint correctly without reading your documentation.");
    }
}
