# Audience Classification Worksheet — Reference Solution

Analyst: Instructor
Date: (session date)
Target API: api-dev-quickstart
Base URL: http://localhost:8080

---

## Part 1: Endpoint Inventory

| Method | Path | Status | Content-Type | In the collection? |
| :--- | :--- | :--- | :--- | :--- |
| GET | /api/v1/health | 200 | application/json | yes |
| GET | /api/v1/greetings/Ada | 200 | application/json | yes |
| POST | /api/v1/health | 405 | application/json | yes (the collection may or may not include this; it is worth trying) |
| GET | /api/v1/accounts/999 | 404 | application/json | no |
| GET | /api-docs | 200 | application/json | no |
| GET | /swagger-ui.html | 302 | (none) | no |

Endpoints not in the collection: 3 (accounts/999, /api-docs, /swagger-ui.html). The collection has 2 or 3 depending on the version; either way, half the endpoints students can reach are undocumented in the collection.

---

## Part 2: Audience Classification

| Endpoint | Audience | Justification |
| :--- | :--- | :--- |
| GET /api/v1/health | Open | No auth required, no sensitive data. A health check is useful to any caller and carries only status and service name. |
| GET /api/v1/greetings/{name} | Open | No auth, returns only a reflected greeting string. This is a demo endpoint with no business data. |
| POST /api/v1/health | Open | Same resource as the health GET. The 405 response carries no data, only an Allow header. |
| GET /api/v1/accounts/999 | Internal | Even though it returns a 404, the error body includes a Spring stack trace with internal class names. A real accounts endpoint would expose balances and owner names that must not leave the org. |
| GET /api-docs | Internal | The OpenAPI document describes every endpoint, parameter, and schema. Publishing it tells an attacker exactly what to probe. In production this is gated behind a gateway or served only on an internal network. |
| GET /swagger-ui.html | Internal | Same rationale as /api-docs. The interactive UI makes exploration trivial. It redirects to /swagger-ui/index.html, which is served by springdoc and should be disabled or gated in production. |

The interesting case is /api-docs and /swagger-ui.html. Some teams publish them openly for developer onboarding. The argument against is that they are a map of your attack surface. The right answer depends on the organisation's threat model, and the point of the exercise is having the conversation, not picking one side.

---

## Part 3: Data Sensitivity Assessment

Endpoint: GET /api/v1/accounts/999 (a hypothetical successful response, or the error body as actually observed)

| Field name | Example value | Safe for Open? | Safe for Partner? | Must stay Internal? |
| :--- | :--- | :--- | :--- | :--- |
| status (from health) | "UP" | yes | yes | no |
| service (from health) | "api-dev-quickstart" | yes | yes | no |
| timestamp (from error) | "2025-..." | yes | yes | no |
| path (from error) | "/api/v1/accounts/999" | yes | yes | no |
| trace (from error) | "org.springframework.web..." | no | no | yes |

The trace field is the clearest leak. It publishes internal Spring class names and method signatures. An attacker uses that to identify framework versions with known vulnerabilities. A partner might see it and lose confidence in the API's security posture.

In a real accounts endpoint, the successful response would include owner name, balance, account status, and possibly internal risk tier or assigned analyst. The owner name and account status might be safe for Partner. The balance might be safe for Partner under an SLA. The risk tier and analyst email must stay Internal.

---

## Part 4: The Sprawl Question

The first thing that breaks is discoverability: no team can answer "does an endpoint for X already exist?" so they build a duplicate. The second is security: an endpoint nobody can list is one nobody audits, so an old endpoint with a known vulnerability stays exposed because nobody remembers it exists. The third is retirement: with no catalog, you cannot deprecate anything because you cannot prove nobody calls it. The teams building the endpoints feel the duplication cost; the security team and the consumers feel the rest.

---

## Stretch Goal

Target API: https://api.github.com

| Endpoint | Audience | Why |
| :--- | :--- | :--- |
| GET /rate_limit | Open | Returns rate-limit counters scoped to the caller's authentication level. Works unauthenticated and reveals nothing about other users. |
| GET /user | Partner (authenticated user) | Requires a personal access token. Returns the authenticated user's profile, private email, and plan details. This is a partner-style relationship: the caller is a named entity under GitHub's terms of service. |

GET /user required authentication (401 without a token). GET /rate_limit leaked nothing without it — it returned a generic unauthenticated rate limit, which is exactly what an Open endpoint should do.
