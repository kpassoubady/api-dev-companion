# API Ecosystem

Reference for what surrounds a shipped API once the contract is real: codegen, layered testing, contract testing, and where security plugs into the pipeline.

---

## Core Idea

82 percent of organizations now run some level of API-first practice, and 25 percent operate as fully API-first; this is already how the industry works, not an aspirational ideal. The OpenAPI spec you wrote against the quickstart project in Days 1-2 is the same artifact that drives everything in this session: codegen, mock servers for consumers, and the contract tests that check your implementation against what it promises. Losing sight of that connection, treating the spec as documentation rather than a live input other tools consume, is how teams give up most of the API-first benefit.

## API-First in Practice: Codegen

Once a contract exists, tools like OpenAPI Generator read it directly to produce server stubs and client SDKs, so design and implementation stay in sync by construction rather than by someone remembering to update both. Frontend teams can build against mocked responses generated from the same spec while backend teams implement the real logic, a decoupling organizations report cutting development time by up to half. The spec springdoc-openapi generates from `GreetingController` and `HealthController` in this course is exactly the kind of document a generator would consume to produce a client library, without hand-writing either side twice.

## The Layered Test Strategy

Pipelines in 2026 typically run tests in three layers rather than one flat suite: unit and contract tests on every push, integration, security, and performance tests inside the CI/CD pipeline itself, and synthetic monitors continuously against production. Lab 2.3's JUnit5 suite occupies the first layer, running locally in IntelliJ exactly as it would run automatically on every commit in a real pipeline.

| Layer | When it runs | What it checks |
| :--- | :--- | :--- |
| Unit and contract | Every push, pre-commit | Does this code and this contract still agree with themselves |
| Integration, security, performance | Inside CI/CD | Does the assembled system behave correctly under real conditions |
| Synthetic monitoring | Continuously, in production | Is the live API still behaving as documented, right now |

## Contract Testing

Unit and Postman tests both check a service against its own expectations, not against what its consumers actually depend on. Contract testing closes that gap.

| Tool | Model | Best fit |
| :--- | :--- | :--- |
| **Spring Cloud Contract** | Provider-owned contracts in Groovy or YAML; generates producer verification tests and a runnable stub | All-Spring-Boot shops reusing existing Maven or Gradle infrastructure, no broker required |
| **Pact** | Consumer-driven; the consumer defines the expected interaction, the provider verifies it via a Pact Broker | Polyglot environments, or where a central "can-i-deploy" gate matters |

Spring Cloud Contract is the more natural fit for this course's all-JVM stack, but Pact is worth recognizing by name since many real organizations mix services built on different platforms.

## Where Security Sits in the Pipeline

Day 3's concept docs already cover authentication, authorization, and rate limiting in depth; this session only needs to show where those controls plug into delivery. Pre-commit hooks catch secrets before code is even pushed, CI gates run dependency and static analysis scans on pull requests, CD gates scan container images before they reach a registry, and slower dynamic scans run against staging or production on a schedule rather than blocking every commit. Automated scanners still cannot reason about business logic on their own, so a flaw like broken object-level authorization needs a targeted test case, not just a clean scanner report.

## The Case for Deep API Knowledge

The costliest API outages rarely trace back to one buggy line; they trace back to an ambiguous or unenforced contract where one overlooked assumption compounds once traffic scales. An engineer who only writes endpoints inherits that risk; one who reasons about the full lifecycle, design, versioning, documentation, security, and testing together, catches it before it ships. The capstone in Lab 2.4 is a compressed rehearsal of exactly that: it asks you to keep the spec, the security rules, and the tests all in sync on the same shared API, which is the entire argument of this session in miniature.

## Common Pitfalls

Letting the spec drift from the implementation because it is treated as documentation rather than a live contract undoes most of what API-first buys you. Running Newman only by hand and never wiring it into CI means regressions are caught by consumers instead of the pipeline. Gating every single commit on slow dynamic scans, instead of reserving them for scheduled runs, either stalls delivery or gets bypassed under deadline pressure. And treating the case for deep API knowledge as a motivational aside rather than a concrete argument misses the point: Lab 2.4 is where that argument gets tested directly.
