# Version Strategy Worksheet

Fill this in for the shared quickstart API's `/api/v1/greetings/{name}` and `/api/v1/health` endpoints. Every answer needs a concrete decision, not a general principle.

Analyst: (sample solution)
Date:

---

## Part 1: Choose a Versioning Strategy

| Strategy | Fits this API? | Why or why not |
| :--- | :--- | :--- |
| URI versioning (`/api/v1/`, `/api/v2/`) | Yes | Already in use, visible in every log line, and this course's audience includes external/partner-style consumers who benefit from a URL they can read and share |
| Header versioning (`X-API-Version`) | Only for internal use | Would hide the version from anyone just reading the URL; not worth switching away from the existing convention |
| Content negotiation (`Accept: vnd...+json`) | No | Most RESTful in theory, least discoverable in practice; overkill for this API's audience |

TODO: Keep URI versioning. It's already the established convention (`/api/v1/`), consistent, and the most debuggable option for a course API students hit directly with curl and Postman.

## Part 2: Classify the Proposed Changes

| Change | Breaking or non-breaking | Ships in |
| :--- | :--- | :--- |
| C1: Add optional "locale" field | Non-breaking | v1 |
| C2: Rename "message" to "greeting" | Breaking | v2 |
| C3: Require "lang" query parameter | Breaking | v2 |
| C4: Reject names over 50 characters | Breaking | v2 |
| C5: Add GET .../formal endpoint | Non-breaking | v1 |
| C6: Add optional "upStatus" alongside "status" | Non-breaking | v1 |
| C7: Change GET /health to POST /health | Breaking | v2 |

## Part 3: Deprecation Plan

Assume `v2` ships and eventually replaces `v1`.

| Phase | What happens | Trigger to move to the next phase |
| :--- | :--- | :--- |
| Soft deprecation | `v1` stays fully functional; add a `Deprecation` header and a changelog entry pointing to `v2` | `v2` has been live and stable for at least one full release cycle |
| Hard deprecation | `v1` gets a lower rate limit and a warning log on every call | Traffic monitoring shows most known consumers have migrated to `v2` |
| Final shutdown | `v1` routes are removed entirely | The published sunset date arrives and any remaining traffic has been individually followed up with |

## Part 4: An Edge Case

TODO: It would be tempting to sneak C2 (renaming "message" to "greeting") into `v1` because it looks like a harmless cosmetic rename. It is not: any consumer parsing the JSON by field name breaks silently the moment the field disappears, with no error to alert them. Cosmetic renames are exactly the kind of change that should always go through a version bump, not around one.

## Part 5: Verdict

TODO: URI versioning stays the strategy here because it is already the convention this API and its consumers expect, and it keeps the version visible without any extra tooling. Once `v2` ships, `v1` gets a minimum of one full release cycle in soft deprecation before any rate limiting begins, so existing partner integrations have real time to move. A partner team building against this API today should build against `v1` as documented, watch for the `Deprecation` header once `v2` exists, and treat any field rename or path change as a signal that `v1` is being retired, not silently patched.

## Stretch Goal

Sample sunset notice: "Effective [date], `/api/v1/` of the quickstart API enters soft deprecation. `/api/v2/` is now the recommended version. `/api/v1/` will continue to function without new features until [date + 1 release cycle], after which it will be rate-limited, and fully retired on [final date]. Contact the API team before that date if your integration cannot migrate in time."

For C7 (changing `GET /health` to `POST /health`) as non-breaking instead: don't change the existing route at all. Add a second, new route (`POST /api/v1/health/check` or similar) for whatever the `POST` behavior was meant to add, and leave `GET /api/v1/health` exactly as it is. The underlying capability ships without touching anything an existing consumer depends on.
