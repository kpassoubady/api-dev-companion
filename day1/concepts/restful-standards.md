# RESTful Standards: Methods, Status Codes and Resource Naming

Reference for the REST conventions this course holds APIs to, and the checklist Lab 1.1 scores against.

---

## Why Standards Instead of Preferences

Conventions in REST exist so that generic machinery works. A cache can serve a `GET` because `GET` is defined as safe. A proxy can retry a `PUT` because `PUT` is defined as idempotent. A gateway can alert on error rates because errors are defined as 4xx and 5xx. Break any of those definitions and the generic machinery silently does the wrong thing, and every consumer has to write bespoke handling for your API specifically.

That is the real cost of skipping standards, and it compounds. Onboarding cost becomes per-API instead of per-organization. Documentation drifts, because nothing about a URL like `/doStuff` is self-describing. Security review restarts from zero for every service. With most API traffic in 2026 coming from programs rather than people, predictable status codes and machine-readable errors matter more than a friendly message inside a `200`.

## Methods and Their Guarantees

| Method | Safe | Idempotent | Use | Success code |
| :--- | :--- | :--- | :--- | :--- |
| GET | yes | yes | Read a resource or collection | 200 |
| HEAD | yes | yes | Metadata only, no body | 200 |
| POST | no | no | Create a subordinate resource, or a non-CRUD action | 201 with `Location`, or 200 |
| PUT | no | yes | Replace the resource at a known URI | 200 or 204 |
| PATCH | no | no | Partial update | 200 or 204 |
| DELETE | no | yes | Remove a resource | 204, or 200 with a body |

Safe means the call does not change server state. Idempotent means repeating it leaves the same state. A `GET` that mutates and a `DELETE` that returns `500` on the second call are the two violations that produce the most mysterious production behaviour, because retries and caches are built on the assumption that neither happens.

## Status Codes Worth Knowing

Learn the families first, then a short list inside each.

| Family | Meaning for the caller | Codes to know |
| :--- | :--- | :--- |
| 2xx | It worked | 200 OK, 201 Created, 202 Accepted, 204 No Content |
| 3xx | Look elsewhere, or use your cache | 301 Moved Permanently, 304 Not Modified |
| 4xx | Your request was wrong, do not retry it unchanged | 400, 401, 403, 404, 405, 409, 422, 429 |
| 5xx | Something failed on my side, a retry may help | 500, 502, 503, 504 |

Three distinctions account for most mistakes. `401` means the request is unauthenticated, so the server does not know who is calling; `403` means the caller is known and not permitted. `400` means the server could not parse the request; `422` means it parsed fine and failed semantic validation. And returning `200 OK` with a body like `{"success": false}` is the single most damaging shortcut on this list, because it defeats every retry policy, every gateway metric, every cache, and every generic client at once.

## Error Bodies Have a Standard Now

RFC 9457, Problem Details for HTTP APIs, replaces RFC 7807 and defines the error shape so consumers stop guessing. It is served as `application/problem+json` and carries `type`, `title`, `status`, `detail`, and `instance`, plus any extensions you need.

```json
{
  "type": "https://api.example.com/problems/insufficient-funds",
  "title": "Insufficient funds",
  "status": 409,
  "detail": "Account 42 has a balance of 12.50 and the transfer requires 100.00",
  "instance": "/api/v1/accounts/42/transfers/8871"
}
```

Spring has supported this since Framework 6.0 through the `ProblemDetail` type, opted into per controller advice. Spring Boot 4 makes it a framework-wide default instead. This course runs on Spring Boot 3.2.5, so the opt-in form is what you will write.

## Resource Naming Rules

These are ordered by how many defects each one catches.

1. Nouns, not verbs. Use `/orders`, never `/getOrders` or `/createOrder`. The verb is already the HTTP method.
2. Plural collections. Use `/accounts/42`, not `/account/42`. One rule applied consistently beats a mixture.
3. Hierarchy expresses containment. `/customers/17/orders` reads as the orders belonging to customer 17.
4. Lowercase with hyphens for multi-word segments. Use `/purchase-orders`, not `/purchaseOrders` or `/purchase_orders`.
5. The path identifies, the query filters and paginates. Use `/orders?status=shipped&page=2&size=25`, not `/orders/shipped`.
6. No file extensions and no trailing slash. Content negotiation belongs in the `Accept` header.
7. Version explicitly. `/api/v1/orders` is the pragmatic default and the one this course's example API uses. Day 3 covers header and media-type versioning and their trade-offs.
8. Do not leak the implementation. A path like `/api/v1/tbl_cust_master` publishes your schema and guarantees that a refactor becomes a breaking change.

## What Lab 1.1 Checks

The lab checklist is this document turned into questions:

- Identify the protocol
- Score resource naming against the eight rules
- Check method usage against the safe and idempotent guarantees
- Check status code correctness, with attention to `401` against `403`, `400` against `422`, and any `200` carrying an error
- Score the error body against RFC 9457
- Check the versioning approach
- Check whether documentation exists at all
