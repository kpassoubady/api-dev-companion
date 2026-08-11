# API Analysis Worksheet

Fill this in for the API you are analysing. Every answer needs evidence: a request you actually sent and the response you actually got. "It looks RESTful" is not an answer.

Analyst:
Date:
Target API:
Base URL:

---

## Part 1: Identify the Protocol

| Question | Your answer | Evidence |
| :--- | :--- | :--- |
| Which protocol family? (REST / SOAP / RPC / GraphQL) | | |
| What made you certain? | | |
| Payload format | | |
| Does the URL name a resource or an operation? | | |
| Is there a machine-readable contract? Where? | | |

TODO: If you concluded REST, name one thing you saw that a SOAP or RPC API would not have done.

## Part 2: Audience Type

| Question | Your answer |
| :--- | :--- |
| Open, Partner, Internal, or Composite? | |
| What authentication is required? | |
| Are there published docs? A deprecation policy? | |
| If this API broke tomorrow, who would you have to notify? | |

## Part 3: Endpoint Inventory

Send each request in Postman. Record what actually came back, not what you expected.

| Method | Path | Status | Content-Type | Notable about the body |
| :--- | :--- | :--- | :--- | :--- |
| GET | /api/v1/health | | | |
| GET | /api/v1/greetings/{name} | | | |
| POST | /api/v1/health | | | |
| GET | /api/v1/accounts/999 | | | |
| GET | /api-docs | | | |
| GET | /swagger-ui.html | | | |

TODO: add at least two more rows of your own.

How many total endpoints did you discover? How many were NOT in the provided Postman collection? Write the number here: ___

If this API had 200 endpoints across three teams and no catalog, which problem would you hit first: duplicate endpoints, unpatched vulnerabilities, or the inability to retire anything? Explain in one sentence.

## Part 4: Resource Naming, Rules 1 to 8

Score each rule. Use "n/a" honestly when the API gives you nothing to judge.

| Rule | Pass / Fail / n/a | Evidence or counter-example |
| :--- | :--- | :--- |
| 1. Nouns, not verbs | | |
| 2. Plural collections | | |
| 3. Hierarchy means containment | | |
| 4. lowercase-with-hyphens | | |
| 5. Path identifies, query filters | | |
| 6. No extensions, no trailing slash | | |
| 7. Version explicitly | | |
| 8. Does not leak the schema | | |

## Part 5: Methods and Their Guarantees

| Question | Your answer | Evidence |
| :--- | :--- | :--- |
| Does any `GET` change state? | | |
| Is `GET` idempotent here? How did you test it? | | |
| What happens on a method the endpoint does not support? | | |
| Is an `Allow` header returned with that response? | | |

## Part 6: Status Codes

| Situation | Status returned | Correct? | What it costs a caller if wrong |
| :--- | :--- | :--- | :--- |
| Successful read | | | |
| Unsupported method | | | |
| Path that does not exist | | | |
| Unauthenticated request (if applicable) | | | |

TODO: Did you find any response that returns `200` while reporting a failure in the body? If yes, quote it.

## Part 7: Error Body Shape

| Question | Your answer |
| :--- | :--- |
| What Content-Type do errors use? | |
| Does the body follow RFC 9457 (`type`, `title`, `status`, `detail`, `instance`)? | |
| Does the body leak anything it should not? Quote the field. | |

## Part 8: Verdict

TODO: Write three to five sentences. Answer these:

1. Which rule did this API break most often?
2. Which deviation would you defend, and on what grounds?
3. Which single change would most improve it for a consumer?

## Part 9: Stretch Goal

Score a second API against the same checklist. Suggested: the GitHub REST API, `https://api.github.com`.

| Rule or check | Quickstart API | Second API |
| :--- | :--- | :--- |
| Protocol | | |
| Versioning approach | | |
| Error body standard | | |
| Rule broken most often | | |

TODO: Name one convention the second API follows that the quickstart API does not, and one where the reverse is true.
