/**
 * Demo - REST Semantics Against a Real Endpoint
 * Day 1 - Session 3
 *
 * Goal: Show that the REST claims from the slides are observable properties of
 * a live HTTP conversation, not conventions we agreed to on paper.
 *
 * Five probes against the quickstart API: a plain GET, the same GET repeated to
 * observe safety and idempotence, a HEAD to show metadata without a body, a
 * POST to a GET-only endpoint to see the server enforce the method, and a GET
 * on a path that does not exist to see 404 arrive without a custom error code.
 * Every outcome is carried by the status line, which is the point.
 *
 * Requires the quickstart API running on port 8080:
 *   cd <workspace>/api-dev-setup/quickstart-project && mvn spring-boot:run
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoRestOverHttp
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class DemoRestOverHttp {

    static final String BASE_URL = "http://localhost:8080";

    static final List<String> HEADERS_OF_INTEREST =
        List.of("content-type", "content-length", "allow");

    static final HttpClient CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    /**
     * Sends an HTTP request to the running quickstart API. Learners see the
     * raw HttpClient API and observe that REST semantics are observable
     * properties of a live HTTP conversation.
     */
    static HttpResponse<String> send(String method, String path, String body) throws Exception {
        HttpRequest.BodyPublisher publisher = body == null
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(body);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .timeout(Duration.ofSeconds(5))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .method(method, publisher)
            .build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Runs a single probe against the API and prints the request, response
     * status, key headers, and body. Learners compare the "watch for"
     * expectation against the actual wire response for each REST claim.
     */
    static void probe(String label, String method, String path, String body, String watchFor) {
        System.out.println("\n--- " + label + " ---");
        System.out.println("  request  : " + method + " " + path);
        try {
            HttpResponse<String> response = send(method, path, body);
            System.out.println("  status   : " + response.statusCode());
            for (String header : HEADERS_OF_INTEREST) {
                response.headers().firstValue(header)
                    .ifPresent(v -> System.out.println("  " + pad(header) + ": " + v));
            }
            String responseBody = response.body();
            System.out.println("  body     : " + (responseBody.isEmpty()
                ? "(empty)"
                : responseBody.length() > 160 ? responseBody.substring(0, 160) + "..." : responseBody));
            System.out.println("  watch    : " + watchFor);
        } catch (Exception e) {
            System.out.println("  FAILED   : " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("  fix      : start the API with mvn spring-boot:run in");
            System.out.println("             api-dev-setup/quickstart-project");
        }
    }

    static String pad(String header) {
        return String.format("%-9s", header);
    }

    /**
     * Calls GET /api/v1/health three times and prints each response body.
     * Learners observe that the same request returns the same result every
     * time -- this is why caches and proxies may safely repeat a GET.
     */
    static void demonstrateIdempotence() {
        System.out.println("\n--- GET is safe and idempotent: call it three times ---");
        try {
            String first = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                HttpResponse<String> response = send("GET", "/api/v1/health", null);
                if (first == null) {
                    first = response.body();
                }
                System.out.printf("  call %d -> %d  %s  %s%n",
                    attempt,
                    response.statusCode(),
                    response.body(),
                    response.body().equals(first) ? "(identical)" : "(CHANGED)");
            }
            System.out.println("  watch    : same state, same answer. This is why a cache or proxy");
            System.out.println("             may repeat a GET on your behalf without asking.");
        } catch (Exception e) {
            System.out.println("  FAILED   : " + e.getMessage());
        }
    }

    /**
     * Orchestrates the demo: five probes against the live quickstart API.
     * Learners see REST claims (safe, idempotent, self-describing responses)
     * confirmed as observable wire properties, not conventions on paper.
     */
    public static void main(String[] args) {
        System.out.println("Probing " + BASE_URL + " to observe REST semantics on the wire.");

        probe("A plain GET on an existing resource",
            "GET", "/api/v1/greetings/Ada", null,
            "200 with a JSON body; the verb was in the method, not the path");

        demonstrateIdempotence();

        probe("HEAD returns metadata with no body",
            "HEAD", "/api/v1/health", null,
            "same status and content-type as GET, empty body");

        probe("POST to a GET-only endpoint",
            "POST", "/api/v1/health", "{}",
            "405 Method Not Allowed, and an Allow header naming what is permitted");

        probe("GET a path that does not exist",
            "GET", "/api/v1/accounts/999", null,
            "404 from the framework; the caller needs no vocabulary of ours to read it");

        probe("The OpenAPI document that describes all of this",
            "GET", "/api-docs", null,
            "the contract, machine-readable; Day 2 is about writing this deliberately");

        System.out.println("\nTakeaway: the method said what to do, the path said what to do it to, and the");
        System.out.println("status code said what happened. A generic client understood every response");
        System.out.println("without knowing anything about this particular service.");
    }
}
