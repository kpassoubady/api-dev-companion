# Documenting APIs: Standards & Tools

Reference for how an OpenAPI document becomes interactive documentation, and what a peer review of that documentation should check. Covers what Lab 1.3 asks you to do.

---

## Core Idea

Documentation generated from the spec stays in sync with the spec by construction, because it has no separate copy to drift. A wiki page describing an endpoint has to be updated by a person who remembers to do it. A doc page rendered from the OpenAPI file updates the moment the file changes. This is why the tooling in this session reads the spec directly instead of maintaining prose alongside it.

## From Annotations to a Live Console

On this course's Spring Boot API, the document is generated at runtime rather than hand-maintained. `springdoc-openapi` inspects your controllers and their annotations and produces the OpenAPI JSON automatically.

| Step | What happens |
| :--- | :--- |
| Annotate | `@Operation`, `@Schema`, and standard Spring MVC annotations describe each endpoint |
| Introspect | springdoc-openapi reads the annotations and class structure at startup |
| Serve the spec | The generated document is served at `/api-docs`, a path this course customizes in `application.properties` (the library default is `/v3/api-docs`) |
| Render | Swagger UI reads that document and renders it at `/swagger-ui.html` |

This course pins `springdoc-openapi` to 2.5.0 in the quickstart API's `pom.xml`, part of the 2.x line that targets Spring Boot 3.x; the 3.x line of the library only targets Spring Boot 4. Using the wrong line against Spring Boot 3.2.5 will simply fail to start, so check the version in `pom.xml` before assuming a fresh tutorial applies.

## Swagger UI vs Redoc

| Tool | Strength | When to reach for it |
| :--- | :--- | :--- |
| **Swagger UI** | Interactive "try it out" console | Exploring and testing endpoints during development, this course's default |
| **Redoc** | Clean three-panel reference layout | Public-facing docs where readability matters more than in-page testing |

Both read the same OpenAPI document, so switching renderers never means rewriting the contract.

## Peer Review as a Contract Review

Reviewing an API contract works the same way as reviewing code: it is a pass over the spec file, not a conversation about vibes. A useful pass checks whether every endpoint has a description, whether every schema field has a description and an example, and whether every realistic non-2xx response is documented and not just the happy path. Docs that only show a `200` response are technically complete and practically dangerous, since they hide exactly the error handling a consumer needs to plan for.

## What Lab 1.3 Checks

Lab 1.3 has you peer review a partner's Lab 1.2 contract, then view it live through Swagger UI. The checklist is the same one above: complete descriptions, documented error responses, and a working interactive console that matches what was written in the spec, not a stale render from an earlier version of it.
