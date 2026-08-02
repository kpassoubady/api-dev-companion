# DevOps in API Development: Overview

Reference for how the OpenAPI contract from Days 1-2 becomes the artifact a real deployment pipeline gates on, and for the culture that makes automated delivery actually work.

---

## Core Idea

API-first means the OpenAPI spec is designed and reviewed before implementation code exists, and every downstream activity, backend implementation, mock servers, generated docs, generated tests, reads from that same spec instead of a sequence of manual handoffs. Teams that work this way ship roughly 2.8 times faster and see 57 percent fewer integration defects than code-first teams. The spec you built in Days 1-2 for the quickstart project's `GreetingController` and `HealthController` endpoints is the contract a pipeline validates against on every push.

## The Four-Stage Pipeline

A typical API CI/CD pipeline runs in four stages. Trigger pulls the code, the spec, and test configuration together the moment someone pushes or opens a pull request. Build and spec validation compiles the application and lints the OpenAPI document for missing schemas or undefined status codes, so a broken contract fails here rather than in production. Deploy to test environment and test pushes the build to staging, confirms a health check passes, then runs unit tests, contract tests, and integration tests. Gate and promote compares results against defined thresholds and either promotes the build or blocks it and notifies the team automatically.

Lab 2.3's JUnit5 suite, run from IntelliJ against the shared quickstart API, is a small-scale stand-in for that third stage: a real pipeline runs the same kind of test suite, just triggered by a commit instead of a click.

## Surefire vs. Failsafe

Maven splits unit and integration tests across two plugins so a pipeline can gate on the fast tests before paying for the slow ones.

| Plugin | Runs during | Test files | Purpose |
| :--- | :--- | :--- | :--- |
| **Surefire** | `test` phase | `*Test.java` | Fast, no external dependencies, run on every build |
| **Failsafe** | `verify` phase | `*IT.java` | Slower, environment-dependent, run only after unit tests pass |

Lab 2.3's `GreetingControllerTest` and `HealthControllerTest` are exactly the kind of unit test Surefire picks up automatically by naming convention alone.

## DORA Metrics and Deployment Health

Deployment frequency, lead time for changes, change failure rate, and mean time to restore are the four DORA metrics used to judge how healthy a delivery pipeline actually is. Elite teams deploy multiple times per day, sometimes per hour, keep lead time under an hour, hold change failure rate at zero to five percent, and restore from failure in under an hour, using canary releases and feature toggles to make frequent deployment safe rather than reckless.

## You Build It, You Run It

The team that writes an API's code also deploys it, monitors it, and gets paged when it breaks. This ownership model, not the tooling, is what the DORA gains actually depend on; automating a pipeline on top of an unchanged handoff-based org structure rarely moves the numbers. Ownership only works with observability: structured logs, latency and error-rate metrics, and distributed traces, all sharing one correlation ID so an incident can be traced across all three signals instead of three disconnected dashboards.

## Common Pitfalls

Treating CI/CD as a tool suite bolted onto an unchanged team structure, instead of the culture and ownership shift it actually requires, is the most common way these gains fail to materialize. A close second is skipping OpenAPI spec validation as a pipeline gate and testing only the implementation, which lets the contract and the running API quietly drift apart. Running only unit tests in CI and treating integration or contract tests as optional defeats the entire point of splitting Surefire from Failsafe. And having Postman collections is not the same as having automated API testing; a collection only becomes a real gate once something runs it headlessly, via Newman, on every build.
