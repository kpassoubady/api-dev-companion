# Peer Review Checklist: Lab 1.2 Spec (worked example)

Reviewer: Instructor
Date: Day 2, Session 2
Spec author: `lab1_2_writing_an_openapi_specification/solution/openapi-quickstart.yaml`

## Completeness

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| `info.title` and `info.description` are both present and specific | Pass | "API Dev Quickstart" plus a description naming both endpoints |
| Every path has a `summary` | Pass | Both `/api/v1/health` and `/api/v1/greetings/{name}` have one |
| Every path has a `description` that says more than the summary | Pass | Descriptions restate the behavior in a full sentence, not a repeat of the summary |
| The `name` path parameter is marked `required` and has its own `description` | Pass | `required: true`, `description: The name to greet.` |
| Every `200` response has a `description` | Pass | "The service is healthy." and "A greeting for the supplied name." |
| Every response references a schema via `$ref`, not an inline shape | Pass | `HealthStatus` and `Greeting` both defined once under `components/schemas` |

## Accuracy

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| The `Greeting` schema's fields match what you actually observed from Postman | Pass | `message` field matches `curl http://localhost:8080/api/v1/greetings/Learner` exactly |
| The example value(s) would actually be returned by the real API | Pass | Confirmed by running the health and greeting requests before writing the spec |

## Coverage

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| Does the spec document anything beyond the happy path (error case, stretch goal)? | Fail | Neither path documents a non-2xx response; this is the stretch goal, not the base requirement |

## Verdict

The base spec is complete and accurate against the real API. The single most useful fix is the one the checklist already flagged: neither endpoint documents what happens on a bad request or an unmapped path, which is exactly the gap a peer reviewer should catch before calling a spec "done." A new consumer could call both endpoints correctly using only this spec, but would have no documented guidance for anything that isn't the happy path.
