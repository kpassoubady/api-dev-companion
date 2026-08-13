/**
 * Demo - Breaking or Non-Breaking? A Checklist
 * Day 3 - Session 1
 *
 * Goal: Run a list of proposed changes to the shared /accounts resource
 * through the breaking/non-breaking checklist, so the classification rule
 * is concrete before Lab 2.1 asks students to apply it themselves.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day3/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoNonBreakingChangeChecklist
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.List;

public class DemoNonBreakingChangeChecklist {

    record ProposedChange(
        String description,
        boolean removesOrRenamesField,
        boolean tightensValidation,
        boolean makesOptionalParamRequired,
        boolean changesPathOrMethod
    ) {
        boolean isBreaking() {
            return removesOrRenamesField || tightensValidation
                || makesOptionalParamRequired || changesPathOrMethod;
        }

        String reason() {
            if (removesOrRenamesField) return "removes/renames an existing field";
            if (tightensValidation) return "tightens a validation rule";
            if (makesOptionalParamRequired) return "makes an optional parameter required";
            if (changesPathOrMethod) return "changes the URL path or HTTP method";
            return "adds something optional, nothing existing changes shape";
        }
    }

    static List<ProposedChange> proposedChanges() {
        return List.of(
            new ProposedChange("Add an optional \"tier\" field to the account response",
                false, false, false, false),
            new ProposedChange("Rename \"owner\" to \"accountHolder\" in the response",
                true, false, false, false),
            new ProposedChange("Require a \"region\" query parameter that used to be optional",
                false, false, true, false),
            new ProposedChange("Reject account IDs shorter than 4 digits (previously allowed)",
                false, true, false, false),
            new ProposedChange("Add a new GET /accounts/{id}/history endpoint",
                false, false, false, false),
            new ProposedChange("Move account lookup from GET /accounts/{id} to GET /v2/accounts/{id}",
                false, false, false, true)
        );
    }

    public static void main(String[] args) {
        System.out.println("Running each proposed change through the breaking/non-breaking checklist:\n");

        int breakingCount = 0;
        for (ProposedChange change : proposedChanges()) {
            String verdict = change.isBreaking() ? "BREAKING" : "non-breaking";
            System.out.printf("  [%-12s] %s%n", verdict, change.description());
            System.out.printf("               -> %s%n", change.reason());
            if (change.isBreaking()) breakingCount++;
        }

        System.out.printf("%n%d of %d proposed changes are breaking and need a new version.%n",
            breakingCount, proposedChanges().size());
        System.out.println("\nTakeaway: the question is never \"did something change,\" it's \"does the");
        System.out.println("existing consumer's code still work unmodified.\" Lab 2.1 applies this same");
        System.out.println("checklist to design a version strategy for an evolving resource.");
    }
}
