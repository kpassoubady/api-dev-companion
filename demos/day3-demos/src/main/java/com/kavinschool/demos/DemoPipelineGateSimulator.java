/**
 * Demo - Pipeline Gate & Promote Simulator
 * Day 4 - Session 1 (DevOps in API Development: Overview)
 *
 * Goal: Simulate the "gate & promote" stage of a 4-stage API CI/CD pipeline
 * (trigger -> build & spec validation -> deploy & test -> gate & promote).
 * Each sample build carries the signals that stage would have already
 * collected (spec valid?, unit tests pass?, coverage %, contract tests
 * pass?); this demo evaluates each build against a promotion threshold and
 * prints PROMOTE or BLOCK with the reason, showing how a broken OpenAPI
 * spec or a failing contract test blocks a release before it ever reaches
 * production.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day4/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoPipelineGateSimulator
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoPipelineGateSimulator {

    static final int MIN_COVERAGE_PERCENT = 80;

    record Build(
        String label,
        boolean specValid,
        boolean unitTestsPass,
        int coveragePercent,
        boolean contractTestsPass
    ) {
        boolean promotable() {
            return specValid && unitTestsPass
                && coveragePercent >= MIN_COVERAGE_PERCENT
                && contractTestsPass;
        }

        String reason() {
            if (!specValid) return "OpenAPI spec failed validation (missing schema or undefined status code)";
            if (!unitTestsPass) return "unit tests failed";
            if (coveragePercent < MIN_COVERAGE_PERCENT) {
                return "coverage " + coveragePercent + "% is below the " + MIN_COVERAGE_PERCENT + "% gate";
            }
            if (!contractTestsPass) return "contract tests failed (implementation drifted from the spec)";
            return "all gates passed";
        }
    }

    static List<Build> sampleBuilds() {
        return List.of(
            new Build("build #101 (greeting endpoint refactor)", true, true, 92, true),
            new Build("build #102 (spec left half-edited before push)", false, true, 88, true),
            new Build("build #103 (new field breaks a consumer contract)", true, true, 90, false),
            new Build("build #104 (rushed patch, thin tests)", true, true, 61, true),
            new Build("build #105 (unit test regression)", true, false, 85, true),
            new Build("build #106 (clean release candidate)", true, true, 95, true)
        );
    }

    public static void main(String[] args) {
        System.out.printf("Gate threshold: spec valid, unit tests pass, coverage >= %d%%, contract tests pass.%n%n",
            MIN_COVERAGE_PERCENT);

        int promoted = 0;
        for (Build build : sampleBuilds()) {
            String verdict = build.promotable() ? "PROMOTE" : "BLOCK  ";
            System.out.printf("  [%s] %s%n", verdict, build.label());
            System.out.printf("            -> %s%n", build.reason());
            if (build.promotable()) promoted++;
        }

        System.out.printf("%n%d of %d builds cleared the gate and would be promoted toward production.%n",
            promoted, sampleBuilds().size());
        System.out.println("\nTakeaway: the gate doesn't care how the build got here, only whether it clears");
        System.out.println("every threshold at once. A broken OpenAPI spec or a failing contract test blocks");
        System.out.println("promotion just as surely as a failing unit test does -- the contract is enforced,");
        System.out.println("not just documented.");
    }
}
