# Day 2 Demos

Instructor demos for Day 2: API contracts, OpenAPI/Swagger, documentation tooling, and contract-first vs code-first.

This is a self-contained Maven project with no third-party dependencies. Every demo uses only the JDK, so it compiles and runs offline on a fresh machine.

| | |
| :--- | :--- |
| **Group / artifact** | `com.kavinschool` / `api-dev-day2-demos` |
| **Package** | `com.kavinschool.demos` |
| **Java release** | 21 |

## Open in an IDE

Open the `day2/demos` folder (the one containing `pom.xml`) as a Maven project. IntelliJ IDEA detects it automatically. Then run any `Demo*` class with the green arrow next to `main`, or right-click the class and choose **Run**.

If IntelliJ shows import errors, right-click `pom.xml` and choose **Maven > Reload project**.

## Run from the Command Line

All commands run from `day2/demos`.

```bash
mvn -q clean compile
```

Then run a demo either way. Using the exec plugin:

```bash
mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoSchemaReuse
```

Or straight from the compiled classes, which starts faster and is the better choice in front of a class:

```bash
java -cp target/classes com.kavinschool.demos.DemoSchemaReuse
```

Running `mvn -q compile exec:java` with no `-Ddemo.class` runs `DemoOpenApiSpecValidator`, the default set in `pom.xml`.

## Demos

| Class | Session | Goal | Needs the API running? |
| :--- | :--- | :--- | :--- |
| `DemoOpenApiSpecValidator` | 1 | Score a valid spec and a broken one against the same checklist: version root, blank fields, dangling `$ref` | No |
| `DemoSchemaReuse` | 1 | Show duplicated inline schemas drifting after one edit, while a `$ref`'d schema cannot | No |
| `DemoDocCompletenessChecker` | 2 | Score five endpoint doc entries for descriptions, examples, and error-response coverage | No |
| `DemoLiveDocsCheck` | 2 | Confirm the shared API serves `/api-docs` and `/swagger-ui.html`, and that `info.version` matches its own paths | Yes |
| `DemoContractFirstMock` | 3 | Serve realistic responses from a mock built purely from the spec's examples, before any backend exists | No |
| `DemoCodeFirstAlwaysInSync` | 3 | Show a springdoc-style registry regenerate its doc immediately after a code change, versus a stale hand-maintained snapshot | No |

Sources are under `src/main/java/com/kavinschool/demos/`.

## Starting the Shared API

One demo talks to the course's Spring Boot API, which lives in the setup repo alongside this one:

```bash
cd ../../../api-dev-setup/quickstart-project
mvn spring-boot:run
```

It listens on port 8080. Leave it running for the whole session. If the port is taken, see [Free Up Port 8080](../../../api-dev-setup/quickstart-project/README.md#free-up-port-8080) in the setup repo.

## Suggested Running Order

`DemoOpenApiSpecValidator` then `DemoSchemaReuse` support the contracts and OpenAPI anatomy block, and hand directly into Lab 1.2, which asks students to write a spec that would pass both checks. `DemoDocCompletenessChecker` then `DemoLiveDocsCheck` support the documentation block: score a spec's completeness offline first, then see the same kind of spec rendered live through springdoc-openapi and Swagger UI. `DemoContractFirstMock` then `DemoCodeFirstAlwaysInSync` close the day by making the contract-first vs code-first tradeoff concrete, one demo per side.
