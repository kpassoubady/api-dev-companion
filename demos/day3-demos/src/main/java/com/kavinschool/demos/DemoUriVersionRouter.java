/**
 * Demo - URI Versioning Keeps v1 Consumers Untouched
 * Day 3 - Session 1
 *
 * Goal: Simulate a tiny in-memory router that dispatches by URI version
 * prefix, the same way separate @RequestMapping("/api/v1/...") and
 * @RequestMapping("/api/v2/...") controllers would in Spring Boot. Shows
 * that adding v2 never changes what v1 returns.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day3/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoUriVersionRouter
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class DemoUriVersionRouter {

    static class VersionedRouter {
        private final Map<String, Function<String, String>> handlersByVersionPrefix = new LinkedHashMap<>();

        void register(String versionPrefix, Function<String, String> handler) {
            handlersByVersionPrefix.put(versionPrefix, handler);
        }

        String dispatch(String path) {
            for (Map.Entry<String, Function<String, String>> entry : handlersByVersionPrefix.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    String id = path.substring(path.lastIndexOf('/') + 1);
                    return entry.getValue().apply(id);
                }
            }
            return "{\"error\":\"no route for " + path + "\"}";
        }
    }

    public static void main(String[] args) {
        VersionedRouter router = new VersionedRouter();

        // v1: the original, minimal shape. Never touched once published.
        router.register("/api/v1/accounts/", id ->
            "{\"id\":" + id + ",\"owner\":\"Ada Lovelace\"}");

        // v2: adds an optional field (non-breaking on its own), served from
        // a separate controller/prefix so v1 keeps its original contract.
        router.register("/api/v2/accounts/", id ->
            "{\"id\":" + id + ",\"owner\":\"Ada Lovelace\",\"tier\":\"GOLD\"}");

        System.out.println("Same underlying account, requested through two versions:\n");

        for (String path : new String[] {
            "/api/v1/accounts/42",
            "/api/v2/accounts/42",
            "/api/v1/accounts/42"
        }) {
            System.out.printf("  GET %-24s -> %s%n", path, router.dispatch(path));
        }

        System.out.println("\nTakeaway: publishing /api/v2/ added a field for new clients without");
        System.out.println("changing a single byte of what /api/v1/ returns. That's what a version");
        System.out.println("strategy buys you: room to evolve without a coordinated client migration.");
    }
}
