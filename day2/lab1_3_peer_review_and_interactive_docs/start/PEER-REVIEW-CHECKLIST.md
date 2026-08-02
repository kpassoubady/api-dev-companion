# Peer Review Checklist: Lab 1.2 Spec

Reviewer:
Date:
Spec author:

Score each item Pass / Fail / n/a. Every Fail needs a one-line reason, the same evidence discipline as Lab 1.1.

## Completeness

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| `info.title` and `info.description` are both present and specific | | |
| Every path has a `summary` | | |
| Every path has a `description` that says more than the summary | | |
| The `name` path parameter is marked `required` and has its own `description` | | |
| Every `200` response has a `description` | | |
| Every response references a schema via `$ref`, not an inline shape | | |

## Accuracy

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| The `Greeting` schema's fields match what you actually observed from Postman | | |
| The example value(s) would actually be returned by the real API | | |

## Coverage

| Check | Pass / Fail / n/a | Evidence or reason |
| :--- | :--- | :--- |
| Does the spec document anything beyond the happy path (error case, stretch goal)? | | |

## Verdict

TODO: Write two to three sentences.

1. What is the single most useful fix you would ask this author to make?
2. Would a new consumer, who has never seen this API, be able to call it correctly using only this spec?
