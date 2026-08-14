/**
 * Demo - Contract Drift Detector
 * Day 4 - Session 2 (API Ecosystem)
 *
 * Goal: Show what a contract test actually checks. This demo defines a
 * small "spec" (expected field names and types for a response), modeled on
 * this course's own GreetingController and HealthController response
 * shapes, then compares it against a few sample "actual" responses,
 * reporting field-level drift: a missing field, an extra field, or a field
 * whose type no longer matches the spec.
 *
 * This is the same class of check tools like Dredd/Step CI or a Pact/Spring
 * Cloud Contract verification run automatically in a pipeline -- the spec
 * is the enforceable contract, not just documentation.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day4/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoContractDriftDetector
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoContractDriftDetector {

    record FieldSpec(String name, Class<?> type) {
    }

    record ResponseSpec(String endpoint, List<FieldSpec> fields) {
    }

    record SampleResponse(String label, String endpoint, Map<String, Object> body) {
    }

    // Modeled on GET /api/v1/greetings/{name} -> {"message": "..."}
    static final ResponseSpec GREETING_SPEC = new ResponseSpec(
        "GET /api/v1/greetings/{name}",
        List.of(new FieldSpec("message", String.class))
    );

    // Modeled on GET /api/v1/health -> {"status": "UP", "service": "..."}
    static final ResponseSpec HEALTH_SPEC = new ResponseSpec(
        "GET /api/v1/health",
        List.of(new FieldSpec("status", String.class), new FieldSpec("service", String.class))
    );

    static List<SampleResponse> sampleResponses() {
        Map<String, Object> greetingOk = new LinkedHashMap<>();
        greetingOk.put("message", "Hello, Learner! Your API dev setup is working.");

        Map<String, Object> greetingMissingField = new LinkedHashMap<>();
        // "message" was renamed to "greeting" without a version bump.
        greetingMissingField.put("greeting", "Hello, Learner!");

        Map<String, Object> healthOk = new LinkedHashMap<>();
        healthOk.put("status", "UP");
        healthOk.put("service", "api-dev-quickstart");

        Map<String, Object> healthExtraField = new LinkedHashMap<>();
        healthOk.forEach(healthExtraField::put);
        healthExtraField.put("uptimeSeconds", 4821);

        Map<String, Object> healthWrongType = new LinkedHashMap<>();
        healthWrongType.put("status", 200); // should be a String like "UP", not a status code
        healthWrongType.put("service", "api-dev-quickstart");

        return List.of(
            new SampleResponse("greeting: matches spec", "GET /api/v1/greetings/{name}", greetingOk),
            new SampleResponse("greeting: field renamed", "GET /api/v1/greetings/{name}", greetingMissingField),
            new SampleResponse("health: matches spec", "GET /api/v1/health", healthOk),
            new SampleResponse("health: undocumented extra field", "GET /api/v1/health", healthExtraField),
            new SampleResponse("health: status changed type", "GET /api/v1/health", healthWrongType)
        );
    }

    static List<String> detectDrift(ResponseSpec spec, Map<String, Object> actual) {
        List<String> drift = new java.util.ArrayList<>();

        for (FieldSpec field : spec.fields()) {
            if (!actual.containsKey(field.name())) {
                drift.add("missing field \"" + field.name() + "\" (spec requires " + field.type().getSimpleName() + ")");
                continue;
            }
            Object value = actual.get(field.name());
            if (!field.type().isInstance(value)) {
                drift.add("field \"" + field.name() + "\" is " + value.getClass().getSimpleName()
                    + ", spec requires " + field.type().getSimpleName());
            }
        }

        List<String> specFieldNames = spec.fields().stream().map(FieldSpec::name).toList();
        for (String actualField : actual.keySet()) {
            if (!specFieldNames.contains(actualField)) {
                drift.add("extra field \"" + actualField + "\" not declared in the spec");
            }
        }

        return drift;
    }

    static ResponseSpec specFor(String endpoint) {
        return endpoint.contains("health") ? HEALTH_SPEC : GREETING_SPEC;
    }

    public static void main(String[] args) {
        System.out.println("Comparing sample responses against the spec's expected field names and types:\n");

        int driftCount = 0;
        for (SampleResponse sample : sampleResponses()) {
            ResponseSpec spec = specFor(sample.endpoint());
            List<String> drift = detectDrift(spec, sample.body());

            String verdict = drift.isEmpty() ? "OK   " : "DRIFT";
            System.out.printf("  [%s] %s (%s)%n", verdict, sample.label(), sample.endpoint());
            if (drift.isEmpty()) {
                System.out.println("            -> response matches the spec");
            } else {
                for (String issue : drift) {
                    System.out.println("            -> " + issue);
                }
                driftCount++;
            }
        }

        System.out.printf("%n%d of %d sample responses drifted from their spec.%n",
            driftCount, sampleResponses().size());
        System.out.println("\nTakeaway: a contract test doesn't ask \"did the endpoint return 200,\" it asks");
        System.out.println("\"does the shape of the response still match what consumers were promised.\" A");
        System.out.println("renamed field, an undeclared extra field, or a changed type is invisible to a");
        System.out.println("basic status-code check but is exactly what breaks a consumer in production.");
    }
}
