/**
 * Demo - One Resource, Three Audiences
 * Day 1 - Session 2
 *
 * Goal: Show that Open, Partner, and Internal are not three different APIs but
 * three deliberate projections of the same resource.
 *
 * A single internal Account record is projected for each audience. The demo
 * prints what each caller sees, then flags what would leak if the projection
 * step were skipped and the internal record were serialised directly. That
 * skipped step is the most common cause of accidental data exposure in a
 * public API.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoAudienceProjections
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoAudienceProjections {

    /** The full internal record. Never leaves the organisation as-is. */
    record Account(
        long id,
        String displayName,
        String legalName,
        String taxId,
        String status,
        double balance,
        String internalRiskTier,
        String assignedAnalystEmail,
        String storageShardKey
    ) {}

    enum Audience { OPEN, PARTNER, INTERNAL }

    static final Account ACCOUNT = new Account(
        42L,
        "Ada L.",
        "Ada Lovelace",
        "TAX-9981-2277",
        "ACTIVE",
        1250.00,
        "TIER_3_ELEVATED",
        "analyst.dev@bank.example",
        "shard-eu-west-07"
    );

    /**
     * Projects a full internal record into the subset visible to a given
     * audience. Learners see that "Open API" is not a separate system -- it is
     * the same record with fewer fields in the response.
     */
    static Map<String, Object> project(Account a, Audience audience) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", a.id());
        view.put("displayName", a.displayName());
        view.put("status", a.status());

        if (audience == Audience.PARTNER || audience == Audience.INTERNAL) {
            view.put("legalName", a.legalName());
            view.put("balance", a.balance());
        }
        if (audience == Audience.INTERNAL) {
            view.put("taxId", a.taxId());
            view.put("internalRiskTier", a.internalRiskTier());
            view.put("assignedAnalystEmail", a.assignedAnalystEmail());
            view.put("storageShardKey", a.storageShardKey());
        }
        return view;
    }

    /**
     * Returns the consumer profile for each audience type: who calls, how they
     * authenticate, and what guarantees they get. Learners map the three
     * audience labels to real-world access patterns.
     */
    static String describe(Audience audience) {
        return switch (audience) {
            case OPEN -> "anyone who signs up | API key | public docs | published deprecation policy";
            case PARTNER -> "named orgs under contract | OAuth client credentials or mTLS | SLA";
            case INTERNAL -> "teams inside the org | service identity behind the gateway";
        };
    }

    /**
     * Prints the same account through all three audience projections side by
     * side. Learners count the fields and see the progressive disclosure:
     * OPEN (3 fields), PARTNER (5), INTERNAL (9).
     */
    static void printProjections() {
        for (Audience audience : Audience.values()) {
            Map<String, Object> view = project(ACCOUNT, audience);
            System.out.println("\n--- " + audience + " API ---");
            System.out.println("  Consumer: " + describe(audience));
            System.out.println("  Fields exposed: " + view.size() + " of 9");
            view.forEach((k, v) -> System.out.printf("    %-22s %s%n", k, v));
        }
    }

    /**
     * Lists the four internal-only fields that would leak if the projection
     * step were omitted. Learners see the concrete damage: taxId, risk tier,
     * analyst email, and especially storageShardKey, which exposes topology.
     */
    static void printLeakIfProjectionSkipped() {
        List<String> internalOnly = List.of(
            "taxId", "internalRiskTier", "assignedAnalystEmail", "storageShardKey"
        );
        System.out.println("\n--- If you skip the projection and serialise the record directly ---");
        System.out.println("  A public GET /api/v1/accounts/42 would publish " + internalOnly.size()
            + " internal fields:");
        internalOnly.forEach(f -> System.out.println("    leaked: " + f));
        System.out.println("  storageShardKey is the worst of them: it publishes your topology,");
        System.out.println("  so a later re-shard becomes a breaking change to a public contract.");
    }

    /**
     * Orchestrates the demo: one resource, three projections, then the
     * leakage warning. Learners walk away understanding that audience type is
     * a field-by-field design choice, not a separate codebase.
     */
    public static void main(String[] args) {
        System.out.println("One internal resource: Account 42 (9 fields)");
        System.out.println("Three audiences, three deliberate projections of it.");

        printProjections();
        printLeakIfProjectionSkipped();

        System.out.println("\nTakeaway: audience type is a design decision you make field by field.");
        System.out.println("Open, Partner, and Internal differ in who calls, what they see, and what a");
        System.out.println("breaking change costs you - not in the resource behind them.");
    }
}
