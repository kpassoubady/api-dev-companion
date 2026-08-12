/**
 * Demo - A Mock Server Built Purely From the Contract
 * Day 2 - Session 3
 *
 * Goal: Show the parallelism payoff of contract-first directly. A consumer
 * gets realistic responses from a mock built only from the Lab 1.2 spec's
 * examples, with no backend implementation existing yet.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoContractFirstMock
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoContractFirstMock {

    record Example(int status, String body) {}
    record OperationSpec(String method, String path, Example example) {}

    /** Returns a spec-only operation list — no backend implementation exists, just the contract's examples. */
    static List<OperationSpec> contractOnly() {
        return List.of(
            new OperationSpec("GET", "/accounts/42",
                new Example(200, "{\"id\":42,\"owner\":\"Ada Lovelace\",\"status\":\"ACTIVE\"}")),
            new OperationSpec("GET", "/accounts/999",
                new Example(404, "{\"type\":\".../not-found\",\"title\":\"Account not found\",\"status\":404}")),
            new OperationSpec("POST", "/accounts",
                new Example(201, "{\"id\":43,\"owner\":\"Grace Hopper\",\"status\":\"ACTIVE\"}"))
        );
    }

    /** Driven entirely by the spec's examples. No backend logic exists yet. */
    static class SpecDrivenMockServer {
        private final Map<String, Example> byKey = new LinkedHashMap<>();

        /** Builds a mock server keyed by method+path from the spec's operation definitions. */
        SpecDrivenMockServer(List<OperationSpec> spec) {
            for (OperationSpec op : spec) {
                byKey.put(op.method() + " " + op.path(), op.example());
            }
        }

        /** Resolves a call against the spec's examples. Returns 501 if no example was defined for this method+path. */
        Example call(String method, String path) {
            Example ex = byKey.get(method + " " + path);
            if (ex == null) {
                return new Example(501, "{\"error\":\"No example in the spec for " + method + " " + path + "\"}");
            }
            return ex;
        }
    }

    /**
     * Demonstrates the parallelism payoff of contract-first: a mock server built purely from
     * the Lab 1.2 spec's examples returns realistic responses for every documented operation,
     * so a frontend team or Postman can build and test with no backend implementation yet.
     */
    public static void main(String[] args) {
        System.out.println("A consumer built against the Lab 1.2 contract, before any server code exists.");

        SpecDrivenMockServer mock = new SpecDrivenMockServer(contractOnly());

        for (Map.Entry<String, String> call : List.of(
            Map.entry("GET", "/accounts/42"),
            Map.entry("GET", "/accounts/999"),
            Map.entry("POST", "/accounts"),
            Map.entry("DELETE", "/accounts/42")
        )) {
            Example response = mock.call(call.getKey(), call.getValue());
            System.out.printf("  %-6s %-16s -> %d %s%n", call.getKey(), call.getValue(), response.status(), response.body());
        }

        System.out.println("\nTakeaway: every call above resolved from the spec's examples alone.");
        System.out.println("A frontend team, or Postman, can build and test against this today, while a");
        System.out.println("backend team implements the real thing against the same contract in parallel.");
    }
}
