/**
 * Demo - Test Pyramid Runner
 * Day 4 - Session 2 (API Ecosystem)
 *
 * Goal: Simulate running the layered test strategy in order -- unit ->
 * integration -> contract -- with illustrative timing, to make the
 * fail-fast principle concrete: if a fast layer fails, the pipeline skips
 * the expensive layers below it and reports immediately, instead of
 * running everything regardless of outcome.
 *
 * Durations are illustrative simulated numbers, not real elapsed time --
 * there's no System.currentTimeMillis() and no actual sleeping here.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day4/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoTestPyramidRunner
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoTestPyramidRunner {

    record Layer(String name, String tooling, int simulatedSeconds) {
    }

    // Unit -> integration -> contract: cheapest and fastest first, most
    // expensive and slowest last, exactly as the research's three-layer
    // test model recommends for a 2026 pipeline.
    static List<Layer> layers() {
        return List.of(
            new Layer("unit", "JUnit5, no external dependencies", 3),
            new Layer("integration", "@SpringBootTest, real app context", 22),
            new Layer("contract", "Postman/Newman collection run against the spec", 15)
        );
    }

    static void runLayers(String scenario, String failingLayer) {
        System.out.println(scenario);
        int elapsedSeconds = 0;
        for (Layer layer : layers()) {
            boolean fails = layer.name().equals(failingLayer);
            elapsedSeconds += layer.simulatedSeconds();
            String outcome = fails ? "FAILED" : "passed";
            System.out.printf("  [%-11s] %s -> %s in ~%ds (cumulative ~%ds)%n",
                layer.name(), layer.tooling(), outcome, layer.simulatedSeconds(), elapsedSeconds);
            if (fails) {
                int skippedSeconds = layers().stream()
                    .skip(layers().indexOf(layer) + 1L)
                    .mapToInt(Layer::simulatedSeconds)
                    .sum();
                System.out.printf("  Fail fast: reporting now instead of spending another ~%ds on lower layers.%n",
                    skippedSeconds);
                return;
            }
        }
        System.out.printf("  All layers passed. Total pipeline time ~%ds.%n", elapsedSeconds);
    }

    public static void main(String[] args) {
        System.out.println("Layered test strategy: unit -> integration -> contract, cheapest first.\n");

        runLayers("Scenario 1: a unit test fails immediately", "unit");
        System.out.println();
        runLayers("Scenario 2: units pass, an integration test fails", "integration");
        System.out.println();
        runLayers("Scenario 3: every layer passes", "none");

        System.out.println("\nTakeaway: fail fast means the pipeline never runs a slower layer after a faster");
        System.out.println("one already failed. A unit test failure is reported in ~3 seconds instead of ~40;");
        System.out.println("that's the same principle Lab 2.3's JUnit5 suite rehearses at small scale before");
        System.out.println("the capstone puts the whole lifecycle -- design, versioning, security, testing --");
        System.out.println("together in one project.");
    }
}
