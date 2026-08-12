/**
 * Demo - Duplicated Inline Schemas vs a Referenced components/schemas Entry
 * Day 2 - Session 1
 *
 * Goal: Show that adding a field to a schema used by three paths is either safe
 * or silently inconsistent, purely depending on whether the schema was
 * duplicated inline or defined once and referenced with $ref.
 *
 * No API key needed. No network. Pure JDK.
 *
 * Run from day2/demos:
 *   mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoSchemaReuse
 * Or run this class from your IDE.
 */
package com.kavinschool.demos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DemoSchemaReuse {

    record FieldDef(String name, String type) {}
    record SchemaShape(String name, List<FieldDef> fields) {}

    /** Returns three independent copies of the same Account schema, one per path — the duplicated-inline pattern. */
    static Map<String, SchemaShape> duplicatedCopies() {
        Map<String, SchemaShape> copies = new LinkedHashMap<>();
        copies.put("/accounts/{id} (GET)", new SchemaShape("Account", new ArrayList<>(List.of(
            new FieldDef("id", "integer"), new FieldDef("owner", "string"), new FieldDef("status", "string")
        ))));
        copies.put("/accounts (POST body)", new SchemaShape("Account", new ArrayList<>(List.of(
            new FieldDef("id", "integer"), new FieldDef("owner", "string"), new FieldDef("status", "string")
        ))));
        copies.put("/accounts/{id} (PUT body)", new SchemaShape("Account", new ArrayList<>(List.of(
            new FieldDef("id", "integer"), new FieldDef("owner", "string"), new FieldDef("status", "string")
        ))));
        return copies;
    }

    /** Prints each path-to-schema mapping so students can visually compare field lists. */
    static void printShapes(String label, Map<String, SchemaShape> shapes) {
        System.out.println("\n--- " + label + " ---");
        shapes.forEach((where, shape) -> System.out.println("  " + where + " -> " + shape.fields()));
    }

    /** Returns true only when every entry in the map has the same field list — no drift. */
    static boolean allConsistent(Map<String, SchemaShape> shapes) {
        long distinctShapes = shapes.values().stream().map(SchemaShape::fields).distinct().count();
        return distinctShapes == 1;
    }

    /**
     * Adds a field to the Account shape after three paths already use it. With duplicated inline
     * schemas, editing only one copy leaves the other two silently out of date. With a single
     * $ref to components/schemas, all three paths stay consistent because there is only one copy
     * of the shape to edit.
     */
    public static void main(String[] args) {
        System.out.println("Adding a 'createdAt' field to the Account shape after three paths already use it.");

        Map<String, SchemaShape> duplicated = duplicatedCopies();
        printShapes("Duplicated inline schemas, before the change", duplicated);
        duplicated.get("/accounts/{id} (GET)").fields().add(new FieldDef("createdAt", "string"));
        printShapes("After editing ONE of the three copies", duplicated);
        System.out.println("  Consistent across all three paths? " + allConsistent(duplicated));

        SchemaShape referenced = new SchemaShape("Account", new ArrayList<>(List.of(
            new FieldDef("id", "integer"), new FieldDef("owner", "string"), new FieldDef("status", "string")
        )));
        Map<String, SchemaShape> viaRef = new LinkedHashMap<>();
        viaRef.put("/accounts/{id} (GET)", referenced);
        viaRef.put("/accounts (POST body)", referenced);
        viaRef.put("/accounts/{id} (PUT body)", referenced);

        printShapes("\n$ref to one components/schemas entry, before the change", viaRef);
        referenced.fields().add(new FieldDef("createdAt", "string"));
        printShapes("After editing the ONE referenced schema", viaRef);
        System.out.println("  Consistent across all three paths? " + allConsistent(viaRef));

        System.out.println("\nTakeaway: $ref does not just save typing. It makes drift structurally");
        System.out.println("impossible, because there is only one copy of the shape to edit.");
    }
}
