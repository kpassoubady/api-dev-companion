# Day 1 Demos

Instructor demos for Day 1: API purpose, types, protocols, and REST conventions.

This is a self-contained Maven project with no third-party dependencies. Every demo uses only the JDK, so it compiles and runs offline on a fresh machine.

| | |
| :--- | :--- |
| **Group / artifact** | `com.kavinschool` / `api-dev-day1-demos` |
| **Package** | `com.kavinschool.demos` |
| **Java release** | 21 |

## Open in an IDE

Open the `day1/demos` folder (the one containing `pom.xml`) as a Maven project. IntelliJ IDEA detects it automatically. Then run any `Demo*` class with the green arrow next to `main`, or right-click the class and choose **Run**.

If IntelliJ shows import errors, right-click `pom.xml` and choose **Maven > Reload project**.

## Run from the Command Line

All commands run from `day1/demos`.

```bash
mvn -q clean compile
```

Then run a demo either way. Using the exec plugin:

```bash
mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoStatusCodes
```

Or straight from the compiled classes, which starts faster and is the better choice in front of a class:

```bash
java -cp target/classes com.kavinschool.demos.DemoStatusCodes
```

Running `mvn -q compile exec:java` with no `-Ddemo.class` runs `DemoApiAsContract`, the default set in `pom.xml`.

## Demos

| Class | Session | Goal | Needs the API running? |
| :--- | :--- | :--- | :--- |
| `DemoSetupVerification` | 1 | Confirm the JDK and the shared Spring Boot API are reachable before teaching starts | Yes |
| `DemoApiAsContract` | 2 | Swap the entire provider implementation without touching consumer code | No |
| `DemoAudienceProjections` | 2 | Project one resource for Open, Partner, and Internal audiences, and show what leaks if you skip it | No |
| `DemoProtocolPayloads` | 3 | Print the same operation as REST, SOAP, JSON-RPC, and gRPC, happy path and failure path | No |
| `DemoRestOverHttp` | 3 | Observe REST semantics on the wire: safety, idempotence, 405, 404 | Yes |
| `DemoStatusCodes` | 4 | Compare a correct API against an always-200 API through a retry policy, a cache, and a dashboard | No |
| `DemoResourceNaming` | 4 | Score 15 endpoints against the eight naming rules and print the RESTful rewrite | No |

Sources are under `src/main/java/com/kavinschool/demos/`.

## Starting the Shared API

Two demos talk to the course's Spring Boot API, which lives in the setup repo alongside this one:

```bash
cd ../../../api-dev-setup/quickstart-project
mvn spring-boot:run
```

It listens on port 8080. Leave it running for the whole session. If the port is taken, see [Free Up Port 8080](../../../api-dev-setup/quickstart-project/README.md#free-up-port-8080) in the setup repo.

## Suggested Running Order

`DemoSetupVerification` opens the day and proves the environment works. `DemoApiAsContract` then `DemoAudienceProjections` support the purpose and types block. `DemoProtocolPayloads` then `DemoRestOverHttp` support the protocols block, in that order, so students see the wire formats described before they watch one of them behave. `DemoStatusCodes` and `DemoResourceNaming` close the REST conventions block and hand directly into Lab 1.1, which asks students to build the checker they just watched run.
