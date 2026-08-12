/**
 * Demo - Checking the Live Generated Docs on the Quickstart API
 * Day 2 - Session 2
 *
 * Goal: Confirm the shared Spring Boot API is actually serving springdoc-openapi's
 * generated spec and Swagger UI, and that the declared info.version is consistent
 * with the /api/v1 paths the spec describes.
 *
 * Requires the quickstart API running on port 8080:
 *   cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoLiveDocsCheck
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoLiveDocsCheck {

    static final String BASE_URL = "http://localhost:8080";

    static final HttpClient CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    record Check(String name, boolean passed, String detail, String remedy) {}

    static final List<Check> RESULTS = new ArrayList<>();

    /** Records a single check result with its pass/fail status, detail line, and remedy hint. */
    static void record(String name, boolean passed, String detail, String remedy) {
        RESULTS.add(new Check(name, passed, detail, remedy));
    }

    /** Sends a GET request to the quickstart API and returns the response body as a string. */
    static HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .timeout(Duration.ofSeconds(5))
            .header("Accept", "application/json")
            .GET()
            .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Verifies GET /api-docs returns a 200 with an openapi field, and that info.version matches the /api/v1 paths. */
    static void checkSpecServed() {
        try {
            HttpResponse<String> response = get("/api-docs");
            boolean ok = response.statusCode() == 200 && response.body().contains("\"openapi\"");
            record("OpenAPI document served at /api-docs", ok,
                "GET /api-docs -> " + response.statusCode(),
                "Start the API: cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run");

            if (ok) {
                Matcher m = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"").matcher(response.body());
                String infoVersion = m.find() ? m.group(1) : "(not found)";
                boolean pathsUseV1 = response.body().contains("/api/v1/");
                record("info.version is consistent with the /api/v1 paths", pathsUseV1,
                    "info.version=" + infoVersion + ", paths reference /api/v1 = " + pathsUseV1,
                    "A declared version that drifts from the paths it describes is a documentation bug");
            }
        } catch (Exception e) {
            record("OpenAPI document served at /api-docs", false,
                e.getClass().getSimpleName() + ": " + e.getMessage(),
                "Start the API: cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run");
        }
    }

    /** Verifies that the Swagger UI is reachable at /swagger-ui.html (200 or 302). */
    static void checkSwaggerUiServed() {
        try {
            HttpResponse<String> response = get("/swagger-ui.html");
            boolean ok = response.statusCode() == 200 || response.statusCode() == 302;
            record("Swagger UI reachable at /swagger-ui.html", ok,
                "GET /swagger-ui.html -> " + response.statusCode(),
                "Confirm springdoc-openapi-starter-webmvc-ui is on the classpath");
        } catch (Exception e) {
            record("Swagger UI reachable at /swagger-ui.html", false,
                e.getClass().getSimpleName() + ": " + e.getMessage(),
                "Start the API: cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run");
        }
    }

    /** Prints a PASS/FAIL report table with detail lines and remedy hints for each check. */
    static void printReport() {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("LIVE DOCS CHECK");
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
     * Confirms the running quickstart API serves springdoc-openapi's generated spec at /api-docs
     * and the interactive Swagger UI at /swagger-ui.html, then checks that the declared info.version
     * is consistent with the /api/v1 paths the spec describes.
     */
    public static void main(String[] args) {
        System.out.println("Checking the quickstart API's generated docs at " + BASE_URL);

        checkSpecServed();
        checkSwaggerUiServed();

        printReport();

        System.out.println("\nOpen in a browser: " + BASE_URL + "/swagger-ui.html");
        System.out.println("\nTakeaway: this is the same spec Lab 1.3 peer reviews, just rendered live");
        System.out.println("instead of read as a file.");
    }
}
