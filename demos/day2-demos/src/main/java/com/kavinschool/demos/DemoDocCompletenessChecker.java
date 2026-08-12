/**
 * Demo - Scoring Documentation Completeness
 * Day 2 - Session 2
 *
 * Goal: Score a handful of endpoint doc entries against the checklist a peer
 * reviewer should apply in Lab 1.3: descriptions, examples, and whether error
 * responses are documented at all, not just the 200 path.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoDocCompletenessChecker
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.ArrayList;
import java.util.List;

public class DemoDocCompletenessChecker {

    record ResponseDoc(String status, boolean hasDescription) {}
    record EndpointDoc(String method, String path, boolean hasDescription, boolean hasExample, List<ResponseDoc> responses) {}

    /** Returns five endpoint doc entries spanning fully-documented to happy-path-only, for scoring. */
    static List<EndpointDoc> sampleDocs() {
        return List.of(
            new EndpointDoc("GET", "/accounts/{id}", true, true, List.of(
                new ResponseDoc("200", true), new ResponseDoc("404", true)
            )),
            new EndpointDoc("POST", "/accounts", true, false, List.of(
                new ResponseDoc("201", true), new ResponseDoc("400", true), new ResponseDoc("409", false)
            )),
            new EndpointDoc("GET", "/orders/{id}", false, false, List.of(
                new ResponseDoc("200", false)
            )),
            new EndpointDoc("DELETE", "/orders/{id}", true, false, List.of(
                new ResponseDoc("204", true)
            )),
            new EndpointDoc("PUT", "/orders/{id}", true, true, List.of(
                new ResponseDoc("200", true), new ResponseDoc("404", true), new ResponseDoc("422", true)
            ))
        );
    }

    record Score(String label, int earned, int possible, List<String> gaps) {}

    /**
     * Scores a single endpoint doc entry against the peer-review checklist: endpoint description,
     * examples, non-2xx error coverage, and per-response descriptions. Returns the earned/possible
     * ratio and a list of gaps so students see exactly what a reviewer flags.
     */
    static Score score(EndpointDoc doc) {
        int possible = 0;
        int earned = 0;
        List<String> gaps = new ArrayList<>();

        possible++;
        if (doc.hasDescription()) earned++; else gaps.add("missing endpoint description");

        possible++;
        if (doc.hasExample()) earned++; else gaps.add("missing request/response example");

        boolean has2xx = doc.responses().stream().anyMatch(r -> r.status().startsWith("2"));
        boolean hasNon2xx = doc.responses().stream().anyMatch(r -> !r.status().startsWith("2"));
        possible++;
        if (has2xx && hasNon2xx) earned++; else gaps.add("only documents the happy path, no non-2xx response");

        for (ResponseDoc r : doc.responses()) {
            possible++;
            if (r.hasDescription()) earned++; else gaps.add(r.status() + " response has no description");
        }

        return new Score(doc.method() + " " + doc.path(), earned, possible, gaps);
    }

    /**
     * Scores five endpoint doc entries against a completeness checklist. Shows that an entry
     * can be valid OpenAPI yet score low — the peer reviewer in Lab 1.3 checks more than just
     * "does it parse."
     */
    public static void main(String[] args) {
        System.out.println("Scoring five endpoint doc entries against a completeness checklist:");
        System.out.println("description, example, error coverage, per-response descriptions.\n");

        for (EndpointDoc doc : sampleDocs()) {
            Score s = score(doc);
            System.out.printf("%-20s %d/%d%n", s.label(), s.earned(), s.possible());
            s.gaps().forEach(g -> System.out.println("    - " + g));
        }

        System.out.println("\nTakeaway: an entry can be valid OpenAPI and still score low here.");
        System.out.println("This is what a peer reviewer in Lab 1.3 checks, not just \"does it load\".");
    }
}
