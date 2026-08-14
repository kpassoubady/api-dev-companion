/**
 * Demo - Surefire vs. Failsafe: Fail Fast on the Cheap Tests First
 * Day 4 - Session 1 (DevOps in API Development: Overview)
 *
 * Goal: Show why Maven separates unit tests (Surefire, `test` phase) from
 * integration tests (Failsafe, `verify` phase). This demo runs a fast
 * "unit test" step and a slower "integration test" step in the correct
 * pipeline order, then reruns it with a unit test failure, to show that a
 * pipeline can fail fast on cheap unit tests before ever paying for the
 * expensive integration tests.
 *
 * Durations are illustrative simulated numbers, not real elapsed time --
 * there's no System.currentTimeMillis() and no actual sleeping here.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day4/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoSurefireVsFailsafeOrder
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoSurefireVsFailsafeOrder {

    record TestStep(String phase, String plugin, String label, int simulatedSeconds, boolean passes) {
    }

    /** `mvn verify` runs Surefire's `test` phase first, then Failsafe's `integration-test`/`verify` phases. */
    static List<TestStep> pipelineSteps(boolean unitTestsPass) {
        return List.of(
            new TestStep("test", "Surefire", "unit tests (no external dependencies)", 4, unitTestsPass),
            new TestStep("verify", "Failsafe", "integration tests (*IT, real Spring context + dependencies)", 47, true)
        );
    }

    static void runPipeline(String scenario, boolean unitTestsPass) {
        System.out.println(scenario);
        int elapsedSeconds = 0;
        for (TestStep step : pipelineSteps(unitTestsPass)) {
            elapsedSeconds += step.simulatedSeconds();
            String outcome = step.passes() ? "PASSED" : "FAILED";
            System.out.printf("  [%s phase] %s -> %s in ~%ds (cumulative ~%ds)%n",
                step.phase(), step.plugin() + ": " + step.label(), outcome, step.simulatedSeconds(), elapsedSeconds);
            if (!step.passes()) {
                System.out.printf("  Pipeline stops here -- Failsafe's %ds integration suite never runs.%n",
                    pipelineSteps(unitTestsPass).get(1).simulatedSeconds());
                return;
            }
        }
        System.out.printf("  Both phases passed. Total pipeline time ~%ds.%n", elapsedSeconds);
    }

    public static void main(String[] args) {
        System.out.println("Maven's `mvn verify` lifecycle runs Surefire's `test` phase before Failsafe's");
        System.out.println("`verify` phase, on purpose: unit tests are cheap, integration tests are not.\n");

        runPipeline("Scenario 1: a broken unit test (bad response mapping)", false);
        System.out.println();
        runPipeline("Scenario 2: unit tests clean, integration tests also run", true);

        System.out.println("\nTakeaway: Surefire's *Test classes run first and are fast because they have no");
        System.out.println("external dependencies; Failsafe's *IT classes run second and are slow because they");
        System.out.println("boot a real context. Ordering them this way means a broken build fails in ~4");
        System.out.println("seconds instead of ~51 -- the pipeline never pays for the expensive suite when the");
        System.out.println("cheap one already caught the problem.");
    }
}
