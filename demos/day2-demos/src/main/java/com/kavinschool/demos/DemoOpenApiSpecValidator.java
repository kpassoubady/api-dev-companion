/**
 * Demo - Validating a Hand-Written OpenAPI Spec
 * Day 2 - Session 1
 *
 * Goal: Show that a spec can parse cleanly and still fail the checks that make
 * it usable: the wrong root version field, blank descriptions, and a $ref that
 * points at a schema which was never defined.
 *
 * The two SpecDoc values below stand in for the result of parsing a YAML file;
 * this demo focuses on the validation logic a reviewer or a linter applies
 * afterward, not on YAML parsing itself.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoOpenApiSpecValidator
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DemoOpenApiSpecValidator {

    record SchemaRef(String name) {}
    record ResponseDef(String status, String description, SchemaRef schema) {}
    record OperationDef(String method, String summary, List<ResponseDef> responses) {}
    record PathDef(String path, List<OperationDef> operations) {}

    record SpecDoc(
        String rootVersionField, String rootVersionValue,
        String infoTitle,
        List<PathDef> paths,
        Set<String> definedSchemas
    ) {}

    /** Returns a clean OpenAPI 3.1 spec with correct root field, title, summary, descriptions, and a resolved $ref. */
    static SpecDoc validSpec() {
        SchemaRef accountRef = new SchemaRef("Account");
        return new SpecDoc(
            "openapi", "3.1.0",
            "Accounts API",
            List.of(new PathDef("/accounts/{id}", List.of(
                new OperationDef("GET", "Get an account by id", List.of(
                    new ResponseDef("200", "Account found", accountRef)
                ))
            ))),
            Set.of("Account")
        );
    }

    /** Returns a spec with swagger: "2.0" root, blank info.title, null summary/description, and a dangling $ref. */
    static SpecDoc brokenSpec() {
        SchemaRef ghostRef = new SchemaRef("Order");
        return new SpecDoc(
            "swagger", "2.0",
            "",
            List.of(new PathDef("/orders/{id}", List.of(
                new OperationDef("GET", null, List.of(
                    new ResponseDef("200", null, ghostRef)
                ))
            ))),
            Set.of()
        );
    }

    record Finding(String rule, boolean passed, String detail) {}

    /**
     * Applies a reviewer's checklist: correct openapi version root, non-blank info.title,
     * every operation has a summary, every response has a description, and every $ref resolves.
     */
    static List<Finding> validate(SpecDoc spec) {
        List<Finding> findings = new ArrayList<>();

        boolean rootOk = spec.rootVersionField().equals("openapi") && spec.rootVersionValue().startsWith("3.");
        findings.add(new Finding("Root uses openapi: 3.x, not swagger: 2.0", rootOk,
            spec.rootVersionField() + ": \"" + spec.rootVersionValue() + "\""));

        boolean infoOk = spec.infoTitle() != null && !spec.infoTitle().isBlank();
        findings.add(new Finding("info.title is present", infoOk, infoOk ? spec.infoTitle() : "(blank)"));

        for (PathDef path : spec.paths()) {
            for (OperationDef op : path.operations()) {
                boolean summaryOk = op.summary() != null && !op.summary().isBlank();
                findings.add(new Finding(op.method() + " " + path.path() + " has a summary", summaryOk,
                    summaryOk ? op.summary() : "(blank)"));

                for (ResponseDef resp : op.responses()) {
                    boolean descOk = resp.description() != null && !resp.description().isBlank();
                    findings.add(new Finding(
                        op.method() + " " + path.path() + " " + resp.status() + " has a description", descOk,
                        descOk ? resp.description() : "(blank)"));

                    boolean refOk = spec.definedSchemas().contains(resp.schema().name());
                    findings.add(new Finding(
                        "$ref to " + resp.schema().name() + " resolves", refOk,
                        refOk ? "defined in components/schemas" : "NOT FOUND in components/schemas"));
                }
            }
        }
        return findings;
    }

    /** Validates a spec and prints an OK/FAIL report with a pass count. */
    static void report(String label, SpecDoc spec) {
        System.out.println("\n--- " + label + " ---");
        List<Finding> findings = validate(spec);
        long passed = findings.stream().filter(Finding::passed).count();
        for (Finding f : findings) {
            System.out.printf("  %-4s %-45s %s%n", f.passed() ? "OK" : "FAIL", f.rule(), f.detail());
        }
        System.out.printf("  %d of %d checks passed.%n", passed, findings.size());
    }

    /**
     * Validates a clean 3.1 spec and a broken spec against the same checklist. Shows that a spec
     * which parses cleanly can still fail every check that makes it usable to a consumer or code
     * generator: wrong root version, blank fields, and a $ref that points nowhere.
     */
    public static void main(String[] args) {
        System.out.println("Validating two OpenAPI documents against the same checklist.");

        report("Valid 3.1 spec", validSpec());
        report("Broken spec (swagger 2.0 root, blank fields, dangling $ref)", brokenSpec());

        System.out.println("\nTakeaway: a spec that a YAML parser accepts can still fail every check");
        System.out.println("that actually makes it useful to a consumer or a code generator.");
    }
}
