/**
 * Lab 2.1 - Non-Breaking Version Strategy Checker (solution)
 *
 * Classifies proposed changes to the shared quickstart API's endpoints as
 * breaking or non-breaking, so the version strategy in the worksheet is
 * backed by a repeatable check instead of a gut feeling.
 *
 * Run: java ChangeClassifier.java
 */

import java.util.ArrayList;
import java.util.List;

public class ChangeClassifier {

    record ProposedChange(
        String id,
        String description,
        boolean removesOrRenamesField,
        boolean tightensValidation,
        boolean makesOptionalParamRequired,
        boolean changesPathOrMethod
    ) {}

    record Finding(String rule, String message) {}

    static final List<ProposedChange> PROPOSED_CHANGES = List.of(
        new ProposedChange("C1", "Add an optional \"locale\" field to the greeting response",
            false, false, false, false),
        new ProposedChange("C2", "Rename \"message\" to \"greeting\" in the response body",
            true, false, false, false),
        new ProposedChange("C3", "Require a \"lang\" query parameter that used to be optional",
            false, false, true, false),
        new ProposedChange("C4", "Reject names longer than 50 characters (previously unlimited)",
            false, true, false, false),
        new ProposedChange("C5", "Add a new GET /api/v1/greetings/{name}/formal endpoint",
            false, false, false, false),
        new ProposedChange("C6", "Add an optional \"upStatus\" boolean alongside \"status\" in the health response",
            false, false, false, false),
        new ProposedChange("C7", "Change GET /api/v1/health to POST /api/v1/health",
            false, false, false, true)
    );

    /**
     * A removed or renamed field is always breaking, because any consumer
     * reading the old field name gets nothing back for it.
     */
    static Finding checkFieldRename(ProposedChange c) {
        if (c.removesOrRenamesField()) {
            return new Finding("field rename/removal",
                "a consumer reading the old field name now gets nothing");
        }
        return null;
    }

    /** A tightened validation rule can reject a request the old version accepted. */
    static Finding checkValidationTightening(ProposedChange c) {
        if (c.tightensValidation()) {
            return new Finding("validation tightened",
                "a request the old version accepted, the new version now rejects");
        }
        return null;
    }

    /** An optional parameter becoming required breaks callers who never sent it. */
    static Finding checkRequiredParamAdded(ProposedChange c) {
        if (c.makesOptionalParamRequired()) {
            return new Finding("param now required",
                "an existing caller who never sent this parameter now gets an error");
        }
        return null;
    }

    /** A changed path or method breaks every existing client's request. */
    static Finding checkPathOrMethodChanged(ProposedChange c) {
        if (c.changesPathOrMethod()) {
            return new Finding("path/method changed",
                "every existing client's URL or HTTP verb now 404s or 405s");
        }
        return null;
    }

    static List<Finding> classify(ProposedChange c) {
        List<Finding> findings = new ArrayList<>();
        addIfPresent(findings, checkFieldRename(c));
        addIfPresent(findings, checkValidationTightening(c));
        addIfPresent(findings, checkRequiredParamAdded(c));
        addIfPresent(findings, checkPathOrMethodChanged(c));
        return findings;
    }

    static void addIfPresent(List<Finding> findings, Finding f) {
        if (f != null) findings.add(f);
    }

    static void report() {
        int breakingCount = 0;

        System.out.println("=".repeat(88));
        System.out.println("VERSION SAFETY REPORT");
        System.out.println("=".repeat(88));

        for (ProposedChange c : PROPOSED_CHANGES) {
            List<Finding> findings = classify(c);
            boolean breaking = !findings.isEmpty();
            if (breaking) breakingCount++;

            System.out.printf("%n[%s] %s%n", c.id(), c.description());
            System.out.println("       " + (breaking
                ? "BREAKING, needs a new version"
                : "non-breaking, safe in the current version"));
            for (Finding f : findings) {
                System.out.printf("       -> %s: %s%n", f.rule(), f.message());
            }
        }

        System.out.println("\n" + "-".repeat(88));
        System.out.printf("Proposed changes  : %d%n", PROPOSED_CHANGES.size());
        System.out.printf("Breaking          : %d%n", breakingCount);
        System.out.printf("Non-breaking      : %d%n", PROPOSED_CHANGES.size() - breakingCount);
    }

    public static void main(String[] args) {
        report();
        System.out.println("\nBefore you finish: for every BREAKING change above, decide in the worksheet");
        System.out.println("whether it ships behind a new version, or whether the feature isn't worth");
        System.out.println("the version bump at all. That decision is the actual version strategy.");
    }
}
