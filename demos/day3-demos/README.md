# Day 3 Demos

Instructor demos for Day 3: API lifecycle, versioning, and security standards (authentication, authorization, rate limiting).

This is a self-contained Maven project with no third-party dependencies. Every demo uses only the JDK, so it compiles and runs offline on a fresh machine.

| | |
| :--- | :--- |
| **Group / artifact** | `com.kavinschool` / `api-dev-day3-demos` |
| **Package** | `com.kavinschool.demos` |
| **Java release** | 21 |

## Open in an IDE

Open the `day3/demos` folder (the one containing `pom.xml`) as a Maven project. IntelliJ IDEA detects it automatically. Then run any `Demo*` class with the green arrow next to `main`, or right-click the class and choose **Run**.

If IntelliJ shows import errors, right-click `pom.xml` and choose **Maven > Reload project**.

## Run from the Command Line

All commands run from `day3/demos`.

```bash
mvn -q clean compile
```

Then run a demo either way. Using the exec plugin:

```bash
mvn -q compile exec:java -Ddemo.class=com.kavinschool.demos.DemoUriVersionRouter
```

Or straight from the compiled classes, which starts faster and is the better choice in front of a class:

```bash
java -cp target/classes com.kavinschool.demos.DemoUriVersionRouter
```

Running `mvn -q compile exec:java` with no `-Ddemo.class` runs `DemoNonBreakingChangeChecklist`, the default set in `pom.xml`.

## Demos

| Class | Session | Goal | Needs the API running? |
| :--- | :--- | :--- | :--- |
| `DemoNonBreakingChangeChecklist` | 1 | Classify six proposed API changes as breaking or non-breaking against the checklist | No |
| `DemoUriVersionRouter` | 1 | Show a v2 route adding a field without changing what v1 returns to existing consumers | No |
| `DemoJwtAuthNGate` | 2 | Gate four requests (valid, expired, tampered, missing token) on authentication before anything else runs | No |
| `DemoTokenBucketRateLimiter` | 2 | Implement token bucket rate limiting from scratch: absorb a burst, reject the overflow, refill over time | No |

Sources are under `src/main/java/com/kavinschool/demos/`.

## Suggested Running Order

`DemoNonBreakingChangeChecklist` then `DemoUriVersionRouter` support the lifecycle and versioning block, and hand directly into Lab 2.1, which asks students to design a non-breaking version strategy for the shared resource. `DemoJwtAuthNGate` then `DemoTokenBucketRateLimiter` support the security standards block, making authentication and rate limiting concrete before Lab 2.2 asks students to implement basic security rules on the shared Spring Boot API.
