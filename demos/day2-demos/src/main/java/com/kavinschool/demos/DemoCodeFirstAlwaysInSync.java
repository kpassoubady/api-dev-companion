/**
 * Demo - Code-First Docs Cannot Drift, Because They Are Regenerated
 * Day 2 - Session 3
 *
 * Goal: Show the other half of the tradeoff. A springdoc-style registry
 * regenerates its documentation from the current code on every call, so a
 * new endpoint appears in the generated doc immediately, with no doc edit
 * made. A hand-maintained snapshot, left untouched, silently falls behind.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoCodeFirstAlwaysInSync
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.ArrayList;
import java.util.List;

public class DemoCodeFirstAlwaysInSync {

    record Param(String name, String type, boolean required) {}
    record Endpoint(String method, String path, String summary, List<Param> params) {}

    /** Stands in for annotated Spring MVC controller methods. */
    static class CodeRegistry {
        private final List<Endpoint> endpoints = new ArrayList<>();

        /** Registers an endpoint as if it were annotated with @GetMapping or @PostMapping. */
        void register(Endpoint endpoint) {
            endpoints.add(endpoint);
        }

        /** Stands in for springdoc-openapi's runtime introspection: always reflects the current code. */
        List<Endpoint> generateDoc() {
            return List.copyOf(endpoints);
        }
    }

    /** Pretty-prints the current state of a documentation snapshot so students can visually compare before and after. */
    static void printDoc(String label, List<Endpoint> doc) {
        System.out.println("\n--- " + label + " ---");
        for (Endpoint e : doc) {
            System.out.println("  " + e.method() + " " + e.path() + " (" + e.summary() + ")");
            for (Param p : e.params()) {
                System.out.println("      param " + p.name() + ": " + p.type() + (p.required() ? " [required]" : ""));
            }
        }
    }

    /**
     * Shows that a springdoc-style code-first registry regenerates its documentation from the
     * running code on every call, so a new endpoint appears immediately with no doc edit.
     * A hand-maintained snapshot left untouched silently falls behind.
     */
    public static void main(String[] args) {
        CodeRegistry codeFirst = new CodeRegistry();
        codeFirst.register(new Endpoint("GET", "/orders/{id}", "Get an order by id",
            List.of(new Param("id", "integer", true))));

        List<Endpoint> generatedBefore = codeFirst.generateDoc();
        printDoc("Generated doc, before a code change", generatedBefore);

        // A developer adds a new endpoint directly on the controller. No doc file touched.
        codeFirst.register(new Endpoint("GET", "/orders", "List orders, optionally filtered by status",
            List.of(new Param("status", "string", false))));

        List<Endpoint> generatedAfter = codeFirst.generateDoc();
        printDoc("Generated doc, immediately after the code change", generatedAfter);

        List<Endpoint> staleHandMaintainedDoc = generatedBefore;
        printDoc("A hand-maintained doc snapshot, still unaware of the new endpoint", staleHandMaintainedDoc);

        System.out.println("\nTakeaway: nobody edited a doc to get the second listing above; springdoc-openapi");
        System.out.println("re-introspects the code on every run. The hand-maintained snapshot is now wrong,");
        System.out.println("and nothing about it will tell you that on its own.");
    }
}
