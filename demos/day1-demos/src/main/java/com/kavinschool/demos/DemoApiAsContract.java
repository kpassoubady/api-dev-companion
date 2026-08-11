/**
 * Demo - An API Is a Contract, Not an Implementation
 * Day 1 - Session 2
 *
 * Goal: Show that a consumer written against a contract keeps working when the
 * provider swaps the entire implementation underneath it.
 *
 * The AccountLookup interface is the contract. Two providers implement it: one
 * backed by an in-memory map, one backed by a "remote" source with a cache and
 * a different internal record shape. The consumer code below never changes and
 * is never recompiled against a specific provider. Watch the output: identical
 * consumer calls, identical results, completely different internals.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day1/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoApiAsContract
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DemoApiAsContract {

    /** The contract. Consumers depend on this and on nothing else. */
    interface AccountLookup {
        Optional<Account> findById(long id);
    }

    /** The agreed payload shape. Part of the contract. */
    record Account(long id, String owner, String status) {}

    /**
     * Provider A: everything lives in one map. Simple and honest.
     * Learners see a straightforward implementation -- the contract hides
     * nothing because there is nothing to hide.
     */
    static class InMemoryAccounts implements AccountLookup {
        private static final Map<Long, Account> DATA = Map.of(
            42L, new Account(42L, "Ada Lovelace", "ACTIVE"),
            77L, new Account(77L, "Grace Hopper", "SUSPENDED")
        );

        @Override
        public Optional<Account> findById(long id) {
            return Optional.ofNullable(DATA.get(id));
        }
    }

    /**
     * Provider B: a completely different design. Rows arrive as raw string
     * arrays from a legacy source, column names differ, status is an integer
     * code, and results are cached.
     *
     * Learners see that the consumer is never touched when the entire
     * implementation changes. The contract is the only shared surface.
     */
    static class CachedLegacyAccounts implements AccountLookup {
        private static final Map<Long, String[]> LEGACY_ROWS = Map.of(
            42L, new String[] {"42", "LOVELACE", "ADA", "1"},
            77L, new String[] {"77", "HOPPER", "GRACE", "3"}
        );

        private final Map<Long, Account> cache = new HashMap<>();
        private int sourceReads = 0;

        @Override
        public Optional<Account> findById(long id) {
            if (cache.containsKey(id)) {
                return Optional.of(cache.get(id));
            }
            String[] row = LEGACY_ROWS.get(id);
            if (row == null) {
                return Optional.empty();
            }
            sourceReads++;
            Account translated = new Account(
                Long.parseLong(row[0]),
                titleCase(row[2]) + " " + titleCase(row[1]),
                decodeStatus(row[3])
            );
            cache.put(id, translated);
            return Optional.of(translated);
        }

        int sourceReads() {
            return sourceReads;
        }

        private static String titleCase(String raw) {
            return raw.charAt(0) + raw.substring(1).toLowerCase();
        }

        private static String decodeStatus(String code) {
            return switch (code) {
                case "1" -> "ACTIVE";
                case "3" -> "SUSPENDED";
                default -> "UNKNOWN";
            };
        }
    }

    /**
     * The consumer. Note what is absent: no knowledge of maps, caches, legacy
     * columns, or status codes. It knows the contract.
     *
     * Learners see that the consumer is written once against the interface and
     * works identically against both providers. That is the contract's value.
     */
    static Map<String, String> renderAccountPage(AccountLookup api, long id) {
        Map<String, String> page = new LinkedHashMap<>();
        api.findById(id).ifPresentOrElse(
            account -> {
                page.put("heading", "Account " + account.id());
                page.put("owner", account.owner());
                page.put("badge", account.status().equals("ACTIVE") ? "green" : "amber");
            },
            () -> page.put("heading", "No such account")
        );
        return page;
    }

    /**
     * Runs the same consumer against a given provider for three account IDs.
     * Learners watch identical consumer code produce identical output from
     * two providers with entirely different internals.
     */
    static void runConsumerAgainst(String label, AccountLookup api) {
        System.out.println("\n--- Consumer running against " + label + " ---");
        for (long id : List.of(42L, 77L, 999L)) {
            System.out.printf("  findById(%-3d) -> %s%n", id, renderAccountPage(api, id));
        }
    }

    /**
     * Orchestrates the demo: runs the consumer against both providers, then
     * demonstrates what happens when the contract itself changes. Learners
     * learn that the contract is the line you cannot cross without breaking
     * every consumer.
     */
    public static void main(String[] args) {
        System.out.println("The contract: Optional<Account> findById(long id)");
        System.out.println("The consumer: renderAccountPage(api, id) - written once, never changed");

        runConsumerAgainst("Provider A (in-memory map)", new InMemoryAccounts());

        CachedLegacyAccounts providerB = new CachedLegacyAccounts();
        runConsumerAgainst("Provider B (legacy rows + cache + code translation)", providerB);

        System.out.println("\n--- What changed behind the contract ---");
        System.out.println("  Provider B reordered names, decoded integer status codes, and cached results.");
        System.out.println("  Reads that actually hit the legacy source: " + providerB.sourceReads() + " (of 3 lookups)");
        System.out.println("  Consumer code edits required: 0");

        System.out.println("\n--- Now break the contract instead ---");
        System.out.println("  Rename Account.owner to Account.ownerName and every consumer stops compiling.");
        System.out.println("  Change 'ACTIVE' to 'A' and the badge logic silently turns amber for healthy accounts.");
        System.out.println("  Neither change touched a single line of provider logic.");

        System.out.println("\nTakeaway: you may rewrite anything the contract does not promise, and nothing it does.");
        System.out.println("That asymmetry is the whole value and the whole cost of publishing an API.");
    }
}
