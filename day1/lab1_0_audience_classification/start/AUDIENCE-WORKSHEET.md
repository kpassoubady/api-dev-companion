# Audience Classification Worksheet

Fill this in for the quickstart API. Every answer needs evidence: a request you actually sent and the response you actually got.

Analyst:
Date:
Target API: api-dev-quickstart
Base URL: http://localhost:8080

---

## Part 1: Endpoint Inventory

Send every request in the Postman collection, then try a few paths on your own. Record everything.

| Method | Path | Status | Content-Type | In the collection? |
| :--- | :--- | :--- | :--- | :--- |
| GET | /api/v1/health | | | yes |
| GET | /api/v1/greetings/{name} | | | yes |
| | | | | |
| | | | | |
| | | | | |
| | | | | |

TODO: add rows for every endpoint you found. The collection has at least 4 endpoints. How many did you discover that were NOT in the collection?

**Endpoints not in the collection:** ___ (this is your first sprawl number)

---

## Part 2: Audience Classification

For each endpoint you found, classify it and justify your choice in one sentence.

| Endpoint | Audience | Justification |
| :--- | :--- | :--- |
| GET /api/v1/health | | |
| GET /api/v1/greetings/{name} | | |
| | | |
| | | |

TODO: fill every row. If an endpoint could serve two audiences, pick the narrower one and explain why.

---

## Part 3: Data Sensitivity Assessment

Pick one endpoint you classified as Internal (or the closest to it). List every field in its response body.

Endpoint: ________________________________________

| Field name | Example value | Safe for Open? | Safe for Partner? | Must stay Internal? |
| :--- | :--- | :--- | :--- | :--- |
| | | | | |
| | | | | |
| | | | | |
| | | | | |

TODO: If the quickstart API has no obviously sensitive fields, describe below what a real Internal endpoint would expose that an Open endpoint must not.

---

## Part 4: The Sprawl Question

TODO: Write two or three sentences.

If this API grew to 200 endpoints over two years, spread across three teams, with no single catalog and no audience classification — what would break first? Who would feel it?

---

## Stretch Goal

Target API: ________________________________________

| Endpoint | Audience | Why |
| :--- | :--- | :--- |
| | | |
| | | |

TODO: Which endpoint required authentication? Which one leaked nothing without it?
