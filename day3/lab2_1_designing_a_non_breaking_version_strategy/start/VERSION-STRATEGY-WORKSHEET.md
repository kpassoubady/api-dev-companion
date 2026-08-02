# Version Strategy Worksheet

Fill this in for the shared quickstart API's `/api/v1/greetings/{name}` and `/api/v1/health` endpoints. Every answer needs a concrete decision, not a general principle.

Analyst:
Date:

---

## Part 1: Choose a Versioning Strategy

| Strategy | Fits this API? | Why or why not |
| :--- | :--- | :--- |
| URI versioning (`/api/v1/`, `/api/v2/`) | | |
| Header versioning (`X-API-Version`) | | |
| Content negotiation (`Accept: vnd...+json`) | | |

TODO: The quickstart API already uses URI versioning (`/api/v1/`). Would you keep it for `v2`, or switch strategies? Justify your answer against the public/internal framing from the session.

## Part 2: Classify the Proposed Changes

Run `ChangeClassifier.java`, then record its verdict for each change and decide which version it ships in.

| Change | Breaking or non-breaking | Ships in |
| :--- | :--- | :--- |
| C1: Add optional "locale" field | | |
| C2: Rename "message" to "greeting" | | |
| C3: Require "lang" query parameter | | |
| C4: Reject names over 50 characters | | |
| C5: Add GET .../formal endpoint | | |
| Your own change (C6) | | |
| Your own change (C7) | | |

## Part 3: Deprecation Plan

Assume `v2` ships and eventually replaces `v1`.

| Phase | What happens | Trigger to move to the next phase |
| :--- | :--- | :--- |
| Soft deprecation | | |
| Hard deprecation | | |
| Final shutdown | | |

## Part 4: An Edge Case

TODO: Describe one breaking change you would be tempted to sneak into `v1` anyway (because it seems small), and explain why you should not.

## Part 5: Verdict

TODO: Write three to five sentences answering:

1. Which versioning strategy did you choose, and why?
2. What is your deprecation timeline for `v1` once `v2` ships?
3. What would you tell a partner team building against this API today?

## Stretch Goal

Draft a one-paragraph deprecation/sunset notice you would publish alongside `v2`, aimed at an external partner team still on `v1`. Then pick one of your breaking changes (C6 or C7) and describe how you would ship the same underlying feature as a non-breaking change instead, if that was possible.
