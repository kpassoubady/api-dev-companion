# Breakout Exercise: Lab 1.2 — Writing an OpenAPI Specification

Day 2, Session 1. Budget 30 minutes.

Yesterday you scored someone else's API against the REST conventions. Today you write a contract of your own, before touching any implementation code: a full OpenAPI 3.1 specification for the quickstart API's two real endpoints.

## Objectives

By the end of this lab you will have:

1. Written a complete `info` and `servers` block for a real, running API.
2. Documented a path in full: summary, parameters, response, and a referenced schema.
3. Defined reusable schemas under `components/schemas` and referenced them with `$ref` instead of repeating the shape inline.
4. Produced the exact spec that Lab 1.3 peer-reviews and compares against a code-generated version.

That last objective is the real one. Writing YAML that parses is easy. Writing a spec that tells a consumer everything they need, with nothing duplicated, is the skill this session is teaching.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first.

Start the shared course API and leave it running, so you can confirm your spec matches its real behavior:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

Confirm it answers:

```bash
curl http://localhost:8080/api/v1/health
curl http://localhost:8080/api/v1/greetings/YourName
```

You will write the spec by hand in `start/openapi-quickstart.yaml`. Validate it as you go by pasting it into the [Swagger Editor](https://editor.swagger.io) (or your IDE's OpenAPI plugin) — it will flag YAML syntax errors and unresolved `$ref`s immediately.

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/openapi-quickstart.yaml` | The spec you complete. The health endpoint is done as a worked example; the greeting endpoint and its schema are marked `TODO`. |
| `solution/openapi-quickstart.yaml` | A completed spec for both endpoints. Open it after you have tried. |

## Instructions

### Step 1: Observe the real behavior first (5 minutes)

Send both requests above in Postman and note the exact response shape. Your spec must describe what the API actually returns, not what you assume it returns — the same discipline Lab 1.1 asked for.

### Step 2: Read the worked example (5 minutes)

`start/openapi-quickstart.yaml` already has `/api/v1/health` fully specified: a summary, a description, a 200 response, and a `$ref` to a `HealthStatus` schema defined once under `components/schemas`. Read it before writing anything. This is the pattern you are about to repeat.

### Step 3: Complete the greetings endpoint (15 minutes)

Fill in every `TODO` for `/api/v1/greetings/{name}`:

1. A `summary` and `description` for the operation.
2. The `name` path parameter: mark it `required`, give it a `string` schema and a one-line `description`.
3. A `200` response whose `content` references a `Greeting` schema.
4. The `Greeting` schema itself under `components/schemas`, with a `message` field, matching the response you actually observed in Step 1.

### Step 4: Validate (5 minutes)

Paste your completed file into the Swagger Editor. Fix anything it flags. Then check it against `solution/openapi-quickstart.yaml` — not to copy it, but to see whether your descriptions would actually help a consumer who has never seen this API.

> Stretch: add a `404` response to the greetings path (the real API currently returns a generic Spring error body for unmapped paths — describe what you observe, not what you wish it returned), and add a second example value to the `Greeting` schema.

## Next

Bring this file to Lab 1.3. You will trade it with a partner, peer review it, then compare it against the version springdoc-openapi generates once the real controllers are annotated.

> **Reference:** See `day2/concepts/springdoc-openapi.md` for a complete guide to generating API docs from code — the dependency setup, the commands, the annotation reference, and the contract-first vs. code-first tradeoff.
