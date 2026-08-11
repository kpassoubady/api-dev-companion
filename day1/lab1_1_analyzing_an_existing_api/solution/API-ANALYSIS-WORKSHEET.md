# API Analysis Worksheet (reference solution)

A completed analysis of the course quickstart API. Your findings may differ in wording; what matters is that every claim is backed by a request you sent. The judgement calls in Part 8 are the part worth arguing about in the debrief.

Analyst: reference solution
Date: Day 1
Target API: api-dev-quickstart (Spring Boot 3.2.5, springdoc-openapi 2.5.0)
Base URL: `http://localhost:8080`

---

## Part 1: Identify the Protocol

| Question | Answer | Evidence |
| :--- | :--- | :--- |
| Which protocol family? | REST over HTTP | Resource paths, several HTTP methods honoured, outcome in the status line |
| What made you certain? | The method carried the verb and the status code carried the outcome | `GET /api/v1/health` returned 200; `POST` to the same path returned 405 with `Allow: GET` |
| Payload format | JSON | `Content-Type: application/json` on every 2xx |
| Resource or operation in the URL? | Resource | `/api/v1/greetings/Ada`, no `getGreeting` anywhere |
| Machine-readable contract? | Yes, OpenAPI 3.0.1 | `GET /api-docs` returns the document; Swagger UI at `/swagger-ui/index.html` |

A SOAP or JSON-RPC API would have answered every call on one path with `POST`, and the 405 would not exist because the method would never have been meaningful. That single 405 with an `Allow` header is the strongest evidence of resource orientation.

## Part 2: Audience Type

| Question | Answer |
| :--- | :--- |
| Open, Partner, Internal, or Composite? | Internal, and only by default. Nothing about it restricts callers. |
| Authentication required? | None. Every endpoint is anonymous. |
| Published docs? Deprecation policy? | Generated Swagger UI, yes. Deprecation policy, no. |
| Who would you notify if it broke? | Unknown, and that is the finding. There is no consumer registry, no auth to identify callers, and no logging of who calls what. |

The absence of authentication is expected on Day 1; Day 3 adds it. The absence of any way to enumerate consumers is the more interesting gap, because it is what makes a breaking change unmanageable regardless of audience type.

## Part 3: Endpoint Inventory

| Method | Path | Status | Content-Type | Notable about the body |
| :--- | :--- | :--- | :--- | :--- |
| GET | `/api/v1/health` | 200 | `application/json` | `{"status":"UP","service":"api-dev-quickstart"}` |
| GET | `/api/v1/greetings/Ada` | 200 | `application/json` | Reflects the path variable into `message` |
| POST | `/api/v1/health` | 405 | `application/json` | `Allow: GET` header present, which is correct |
| GET | `/api/v1/accounts/999` | 404 | `application/json` | Spring default error body, includes a `trace` field |
| GET | `/api-docs` | 200 | `application/json` | OpenAPI 3.0.1; `info.version` is `v0` |
| GET | `/swagger-ui.html` | 302 | none | Redirects to `/swagger-ui/index.html` |
| HEAD | `/api/v1/health` | 200 | `application/json` | Empty body, same status and type as `GET` |
| GET | `/api/v1/health` (x3) | 200 | `application/json` | Byte-identical each time |

Total endpoints discovered: 6 distinct paths (health, greetings/{name}, accounts/999, api-docs, swagger-ui.html, plus the HEAD variant). Endpoints not in the provided Postman collection: 3 (accounts/999, /api-docs, /swagger-ui.html). The collection covers only the two happy-path endpoints, which means half the reachable surface is undocumented in the tool the student was given.

If this API had 200 endpoints across three teams and no catalog, the first problem would be duplicate endpoints, because no team can answer "does an endpoint for this already exist?" before building one. The unpatched vulnerabilities follow closely: an endpoint nobody can list is one nobody audits.

## Part 4: Resource Naming, Rules 1 to 8

| Rule | Verdict | Evidence or counter-example |
| :--- | :--- | :--- |
| 1. Nouns, not verbs | Pass | No verb appears in any path segment |
| 2. Plural collections | Pass | `greetings` is plural; `health` is uncountable and conventional as a status endpoint |
| 3. Hierarchy means containment | n/a | The API is one level deep; there is no nesting to judge |
| 4. lowercase-with-hyphens | Pass | `api-docs` and `swagger-ui` both hyphenate; no camelCase segment exists |
| 5. Path identifies, query filters | Pass | `Ada` is a path variable, not `?name=Ada`. No query parameters exist at all |
| 6. No extensions, no trailing slash | Fail | `/swagger-ui.html` carries a file extension |
| 7. Version explicitly | Partial | `/api/v1/...` is correct. `/api-docs` and `/swagger-ui.html` are unversioned |
| 8. Does not leak the schema | Pass | No table or column names appear in any path |

`greetings/{name}` deserves a note. It uses a natural key rather than an opaque id, which means the resource identity is user-supplied text. That is legal REST and a real design risk: renaming a person changes the resource's URI.

## Part 5: Methods and Their Guarantees

| Question | Answer | Evidence |
| :--- | :--- | :--- |
| Does any `GET` change state? | No | Three consecutive `GET /api/v1/health` calls returned byte-identical bodies |
| Is `GET` idempotent here? | Yes | Same test as above; the service holds no mutable state |
| Unsupported method behaviour | Correct | `POST /api/v1/health` returns 405, not 400 or 500 |
| `Allow` header returned? | Yes | `Allow: GET` |

This is the part the API does best. A generic proxy or cache could sit in front of it and behave correctly without any knowledge of the service.

## Part 6: Status Codes

| Situation | Status | Correct? | Cost if wrong |
| :--- | :--- | :--- | :--- |
| Successful read | 200 | Yes | none |
| Unsupported method | 405 with `Allow` | Yes | A 400 here would tell the caller to fix its body rather than its method |
| Path that does not exist | 404 | Yes | A 200 would make every typo look like a successful call |
| Unauthenticated request | n/a | n/a | No auth exists yet; Day 3 adds it |
| Swagger UI entry point | 302 | Yes | Correct for a redirect, though it is a tooling path rather than API surface |

No endpoint returns `200` while reporting a failure in the body. The status codes are the strongest part of this API.

## Part 7: Error Body Shape

| Question | Answer |
| :--- | :--- |
| Content-Type on errors | `application/json`, not `application/problem+json` |
| Follows RFC 9457? | No. The body uses `timestamp`, `status`, `error`, `path`, and `trace`, so none of `type`, `title`, `detail`, or `instance` is present |
| Leaks anything? | Yes. The `trace` field carries a Java stack trace, naming `org.springframework.web.servlet.resource.NoResourceFoundException` and internal class paths |

The stack trace is the one finding here with real consequences. It tells an attacker the framework, the version family, and the internal package structure, and it does so on an unauthenticated 404. The fix is one property (`server.error.include-stacktrace=never`) plus a `@ControllerAdvice` returning `ProblemDetail`.

## Part 8: Verdict

The API gets the mechanics of REST right and the contract details wrong. Methods, status codes, and the `Allow` header are all correct, which means generic infrastructure works against it unmodified. What is missing is everything that describes the API to a consumer rather than to a machine: errors do not follow RFC 9457, the generated OpenAPI document declares `info.version` as `v0` while every path says `v1`, and the document's responses are typed as `*/*` rather than `application/json`, so the contract promises less than the implementation delivers.

The rule broken most often is rule 7, and it is the deviation I would defend. `/api-docs` and `/swagger-ui.html` are springdoc's tooling endpoints, not part of the API's own contract; versioning them would imply a promise about documentation URLs that nobody wants to make. The `/swagger-ui.html` extension is in the same category. Neither is worth changing.

The one change with the highest value for a consumer is the error body: adopt `ProblemDetail`, drop the stack trace, and set `application/problem+json`. It removes a real information leak and makes failures parseable by the same generic client that already handles the success cases correctly. The version mismatch between `info.version` and the URL is a close second, because it is the kind of contradiction that quietly destroys trust in generated documentation.

## Part 9: Stretch Goal

| Rule or check | Quickstart API | GitHub REST API |
| :--- | :--- | :--- |
| Protocol | REST, JSON | REST, JSON, with a GraphQL API alongside |
| Versioning approach | URL path segment, `/api/v1` | Date-based header, `X-GitHub-Api-Version: 2022-11-28`, plus an `Accept` media type |
| Error body standard | Spring default, includes a stack trace | Custom JSON with `message`, `documentation_url`, and `status`; not RFC 9457 either |
| Rate limiting | None | `X-RateLimit-*` headers, 403 or 429 when exceeded |
| Rule broken most often | Rule 7, on tooling paths only | Rule 7 by the course's definition, since the version is not in the path |

GitHub does two things the quickstart API does not. It links every error to documentation, which turns a failure into a self-service fix, and it publishes rate-limit state in headers so a caller can throttle itself before being throttled. The quickstart API does one thing GitHub does not: it keeps the version visible in the URL, so you can tell which contract a logged request was bound to without inspecting headers.

That contrast is the point of the stretch goal. Header versioning keeps URLs clean and makes the version invisible in logs, proxies, and bug reports. Path versioning clutters URLs and makes the version impossible to lose. Day 3 returns to this trade-off.
