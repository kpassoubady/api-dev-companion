# API Contracts: Standards & OpenAPI/Swagger

Reference for what an API contract is, why it needs a standard shape, and how OpenAPI defines that shape. Covers what Lab 1.2 asks you to write.

---

## Core Idea

An API contract is the formal agreement between a provider and a consumer about what a call sends and what it returns. Without a written contract, that agreement lives in email threads, tribal memory, and whatever the code happens to do this week. OpenAPI is the standard shape for writing the agreement down so both sides, and the tooling both sides depend on, read the same thing.

## Why Standards Instead of a Free-Form Doc

A hand-written description of an API drifts, because nothing forces it to match the code, and every reader has to interpret prose differently. A standard machine-readable format fixes both problems: it can be validated, rendered into interactive docs, and fed to code generators, none of which work on a paragraph of English. The cost of skipping this standard scales with the number of consumers, since each one re-derives the contract by trial and error against a running server instead of reading one file.

## Anatomy of an OpenAPI Document

The current stable line is OpenAPI 3.1, with 3.2 as the newest feature release. Every document has four parts worth knowing cold.

| Section | Holds | Example |
| :--- | :--- | :--- |
| **info** | Title, version, contact | `title: Accounts API`, `version: 1.0.0` |
| **servers** | Base URLs the API actually runs on | `https://api.example.com/api/v1` |
| **paths** | Every endpoint, its methods, parameters, and responses | `/orders/{id}: get: ...` |
| **components/schemas** | Reusable data shapes referenced from any path | `Order`, `Customer` |

```yaml
openapi: 3.1.0
info:
  title: Accounts API
  version: 1.0.0
paths:
  /accounts/{id}:
    get:
      summary: Get an account by id
      responses:
        '200':
          description: Account found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Account'
```

A schema defined once in `components` and referenced by `$ref` from every path that needs it keeps the naming and shape consistent across the whole document, which is exactly what a reviewer checks in Lab 1.3.

## What Can Go Wrong

The most common failure is writing the spec after the code, then letting the two drift apart, which defeats the entire point of writing a contract first. A close second is leaving `description` fields blank throughout; every linter tolerates it, but it is what makes generated docs unusable. A third is copying an old `swagger: "2.0"` example instead of the current `openapi: "3.1.0"` root field. Swagger 2.0 is a different, older format, and the two are not interchangeable.

## What Lab 1.2 Checks

The lab asks you to write an OpenAPI 3.1 specification for a basic resource from scratch, so the checklist is this document turned into requirements: a complete `info` block, an accurate `servers` entry, at least one path with a described request and response, and a `components/schemas` entry referenced by `$ref` rather than repeated inline.
