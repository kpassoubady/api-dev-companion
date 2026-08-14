# Day 4 Demos

Instructor demos for Day 4: DevOps in API development (CI/CD pipeline gating, Surefire vs. Failsafe) and the API ecosystem (contract drift detection, the layered test pyramid).

This is a self-contained Maven project with no third-party dependencies. Every demo uses only the JDK, so it compiles and runs offline on a fresh machine.

| | |
| :--- | :--- |
| **Group / artifact** | `com.kavinschool` / `api-dev-day4-demos` |
| **Package** | `com.kavinschool.demos` |
| **Java release** | 21 |

## Open in an IDE

Open the `day4/demos` folder (the one containing `pom.xml`) as a Maven project. IntelliJ IDEA detects it automatically. Then run any `Demo*` class with the green arrow next to `main`, or right-click the class and choose **Run**.

If IntelliJ shows import errors, right-click `pom.xml` and choose **Maven > Reload project**.

## Run from the Command Line

All commands run from `day4/demos`.

```bash
mvn -q clean compile
```

Then run a demo either way. Using the exec plugin:

```bash
mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoPipelineGateSimulator
```

Or straight from the compiled classes, which starts faster and is the better choice in front of a class:

```bash
java -cp target/classes com.kavinschool.demos.DemoPipelineGateSimulator
```

Running `mvn -q compile exec:java` with no `-Ddemo.class` runs `DemoPipelineGateSimulator`, the default set in `pom.xml`.

## Demos

| Class | Session | Goal | Needs the API running? |
| :--- | :--- | :--- | :--- |
| `DemoPipelineGateSimulator` | 1 | Evaluate sample builds against a promotion gate, showing how a broken spec or failing contract test blocks a release | No |
| `DemoSurefireVsFailsafeOrder` | 1 | Run unit tests before integration tests in the correct Maven phase order, failing fast on the cheap tests | No |
| `DemoContractDriftDetector` | 2 | Compare sample responses against a spec's expected fields and types, reporting missing/extra/wrong-type drift | No |
| `DemoTestPyramidRunner` | 2 | Run the unit -> integration -> contract test layers in order, skipping expensive layers once a faster one fails | No |

Sources are under `src/main/java/com/kavinschool/demos/`.

## Suggested Running Order

`DemoPipelineGateSimulator` then `DemoSurefireVsFailsafeOrder` support the DevOps overview block, making the four-stage CI/CD pipeline and the fail-fast reasoning behind Surefire/Failsafe concrete before students apply them in the capstone. `DemoContractDriftDetector` then `DemoTestPyramidRunner` support the API Ecosystem block, showing what a contract test checks and how the layered test strategy runs in practice, which leads directly into the card applications capstone's JUnit5/MockMvc acceptance suite.
