/**
 * Demo - Verifying the Course Environment Against a Live API
 * Day 1 - Session 1
 *
 * Goal: Confirm in one run that the JDK, the running Spring Boot quickstart
 * API, and its OpenAPI surface are all reachable before teaching begins.
 *
 * This is the demo for the welcome block. It doubles as the instructor's answer
 * to "is everyone actually set up?" Each check prints PASS or FAIL with the
 * remedy, so a student who fails one line knows exactly what to fix. Nothing
 * here teaches API design; it teaches that the shared example is alive.
 *
 * Requires the quickstart API running on port 8080:
 *   cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoSetupVerification
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DemoSetupVerification {

    static final String BASE_URL = "http://localhost:8080";

    static final HttpClient CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    record Check(String name, boolean passed, String detail, String remedy) {}

    static final List<Check> RESULTS = new ArrayList<>();

    static void record(String name, boolean passed, String detail, String remedy) {
        RESULTS.add(new Check(name, passed, detail, remedy));
    }

    /**
     * Verifies the JDK version is 17 or newer. Learners confirm their
     * development environment is ready before the course begins; this is the
     * instructor's answer to "is everyone actually set up?"
     */
    static void checkJdk() {
        String version = System.getProperty("java.version");
        int major = Integer.parseInt(version.split("\\.")[0]);
        record(
            "JDK 17 or newer",
            major >= 17,
            "java.version = " + version + " (" + System.getProperty("java.vendor") + ")",
            "Install JDK 21; see api-dev-setup/install/install.md"
        );
    }

    static HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .timeout(Duration.ofSeconds(5))
            .header("Accept", "application/json")
            .GET()
            .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Sends a GET to an endpoint and records PASS/FAIL with a remedy.
     * Learners see that a single script confirms the JDK, the API, and the
     * OpenAPI surface are all reachable -- the shared example is alive.
     */
    static void checkEndpoint(String name, String path, int expectedStatus, String mustContain) {
        try {
            HttpResponse<String> response = get(path);
            boolean statusOk = response.statusCode() == expectedStatus;
            boolean bodyOk = mustContain == null || response.body().contains(mustContain);
            String body = response.body();
            String preview = body.length() > 90 ? body.substring(0, 90) + "..." : body;
            record(
                name,
                statusOk && bodyOk,
                "GET " + path + " -> " + response.statusCode() + "  " + preview,
                statusOk ? "Body did not contain \"" + mustContain + "\""
                         : "Expected " + expectedStatus + ", got " + response.statusCode()
            );
        } catch (Exception e) {
            record(
                name,
                false,
                "GET " + path + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                "Start the API: cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run"
            );
        }
    }

    /**
     * Prints the PASS/FAIL report for all checks with remedies for each
     * failure. Learners who fail a line know exactly what to fix without
     * raising a hand.
     */
    static void printReport() {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("ENVIRONMENT CHECK");
        System.out.println("=".repeat(78));
        for (Check c : RESULTS) {
            System.out.printf("%-6s %s%n", c.passed() ? "PASS" : "FAIL", c.name());
            System.out.println("       " + c.detail());
            if (!c.passed()) {
                System.out.println("       fix: " + c.remedy());
            }
        }
        long passed = RESULTS.stream().filter(Check::passed).count();
        System.out.println("-".repeat(78));
        System.out.printf("%d of %d checks passed.%n", passed, RESULTS.size());
    }

    /**
     * Orchestrates the environment check: JDK version, health endpoint,
     * greeting endpoint, OpenAPI document, and a 404 path. Learners
     * establish that the shared Spring Boot API is the running example
     * for all four days of the course.
     */
    public static void main(String[] args) {
        System.out.println("Verifying the shared course API at " + BASE_URL);

        checkJdk();
        checkEndpoint("Health endpoint responds", "/api/v1/health", 200, "\"status\":\"UP\"");
        checkEndpoint("Greeting endpoint responds", "/api/v1/greetings/Day1", 200, "Hello, Day1");
        checkEndpoint("OpenAPI document is served", "/api-docs", 200, "\"openapi\"");
        checkEndpoint("Unknown path returns 404", "/api/v1/nothing-here", 404, null);

        printReport();

        System.out.println("\nSwagger UI (open in a browser): " + BASE_URL + "/swagger-ui.html");
        System.out.println("\nTakeaway: this one Spring Boot API is the running example for all four days.");
        System.out.println("Day 1 analyses it, Day 2 documents it, Day 3 versions and secures it,");
        System.out.println("and Day 4 tests and ships it.");
    }
}
