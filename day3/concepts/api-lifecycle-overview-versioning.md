# API Lifecycle: Overview & Versioning

Reference for how an API moves from a drafted contract to a retired endpoint, and how versioning lets that happen without breaking the consumers already depending on it.

---

## Core Idea

An API is not a single artifact you ship once; it is a resource with a lifecycle. It gets designed against a contract, developed and tested, published as a live version, then maintained with a stream of changes until it is eventually deprecated and sunset. Versioning exists to manage that maintenance phase: it lets you evolve the API without pulling the ground out from under whoever already built against the version that's live. The distinction that matters most day to day is not "did I make a change," but "was that change breaking or non-breaking."

## Breaking vs. Non-Breaking

| | Non-breaking (safe within a version) | Breaking (requires a new version) |
| :--- | :--- | :--- |
| **Fields** | Adding a new optional response field | Removing or renaming an existing field |
| **Endpoints** | Adding a new endpoint | Changing a URL path or HTTP method |
| **Validation** | Relaxing a validation rule | Tightening a rule that rejects previously valid requests |
| **Params** | Adding a new optional request parameter | Making an optional parameter required |

A consumer's existing integration code should keep working through every non-breaking change. The moment it wouldn't, that change belongs in a new version, not a silent update to the old one.

## Versioning Strategies

Spring Boot supports the same handful of strategies most REST APIs choose from. URI versioning puts the version in the path itself, `/api/v1/orders` versus `/api/v2/orders`, via separate `@RequestMapping` prefixes. It is the most visible option: easy to read in a log line, easy to share in documentation, easy to debug with a browser or `curl`. Header versioning moves the version into a custom header like `X-API-Version: 2`, keeping the URL clean at the cost of being harder to discover without reading the docs. Content negotiation goes further, encoding the version in the `Accept` header as a vendor media type, `Accept: application/vnd.company.api.v2+json`, which is the most theoretically RESTful approach but the least discoverable in practice. A query parameter version is possible too, but it's the easiest for a client to simply forget to send.

In practice, public and partner-facing APIs lean on URI versioning because predictability matters more than purity when the consumers aren't in your control. Internal APIs, where the calling teams are known and can coordinate, can afford header or content-negotiation versioning instead.

## Deprecation as a Process, Not an Event

Retiring an old version follows a three-phase pattern that shows up across most lifecycle-management guidance. Soft deprecation keeps the old version fully functional but adds a warning, whether that's a `Deprecation` HTTP header, a changelog entry, or both. Hard deprecation follows once most consumers have migrated: the old version gets rate-limited or otherwise degraded to push out stragglers. Final shutdown removes it entirely, on a sunset date that was published in advance. Skipping the soft-deprecation phase, cutting a version off without warning, is one of the most commonly cited causes of broken partner integrations.

## Common Pitfalls

Bumping the major version for every change, including non-breaking ones, trains API consumers to stop paying attention to version numbers at all. A version strategy that stops at "we have a number in the URL" isn't really a strategy; it also needs to define how long old versions stay supported and how their deprecation gets communicated.
