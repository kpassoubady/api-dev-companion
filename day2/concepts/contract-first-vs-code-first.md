# Contract-First vs. Code-First

Reference for the two orders in which a spec and its implementation can be written, and the tradeoffs each one commits you to.

---

## Core Idea

Contract-first means writing the OpenAPI specification before any implementation exists, then building code and client integrations against that agreed shape. Code-first means writing the implementation first and generating the specification from it afterward, which is what `springdoc-openapi` does on this course's Spring Boot API. Neither is universally correct; each trades coordination cost for drift risk in the opposite direction.

## The Tradeoff

| | Contract-first | Code-first |
| :--- | :--- | :--- |
| **Sequence** | Spec written first, code implements it | Code written first, spec generated from it |
| **Source of truth** | The `.yaml` file | The running code |
| **Parallelism** | Frontend, backend, and QA can all start immediately from the same contract | Frontend waits until code exists or an early stub is available |
| **Drift risk** | Spec and implementation can diverge unless enforced by contract testing | Docs auto-track the code, but the design decision happens implicitly while coding |

## Why 2026 Reporting Favors Contract-First

Industry reporting on API-first adoption describes contract-first as the practice that persisted: teams write the OpenAPI spec first, and other teams start building against it, including mocking it in Postman, before a single line of server code exists. The payoff is parallelism. Separate teams, or in this course separate lab groups, build against the same agreed contract without waiting on each other. The cost is that nothing forces the eventual implementation to match the spec unless something checks it, which is why contract-first shops commonly add automated contract tests that compare live responses against the committed document.

## Code-First Still Has a Place

Code-first remains a reasonable default for a small team or an early-stage API where hand-authoring a spec before any code exists is overhead without a payoff yet, since there is no second team waiting to build in parallel. The spec in that case is documentation of what already works rather than a plan for what should exist, and it never drifts because springdoc-openapi regenerates it from the code on every run.

## Making the Distinction Concrete

Postman is where the difference stops being abstract. Point Postman at a mock server built purely from a Lab 1.2 spec, and you are consuming a contract-first API before any backend exists. Point Postman at the running Spring Boot service and its springdoc-openapi-generated Swagger UI, and you are consuming a code-first API where the docs are a byproduct of the implementation. Both are valid ways to expose the same information; the difference is which one existed first and which one is authoritative when they disagree.
