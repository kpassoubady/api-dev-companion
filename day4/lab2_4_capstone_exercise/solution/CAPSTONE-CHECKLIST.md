# Capstone Checklist: Design & Versioning

Fill this in for the new `GET /api/v1/farewells/{name}` endpoint you are adding to the shared quickstart API. This reuses Lab 2.1's breaking/non-breaking checklist — you are not re-learning the criteria, just applying them to your own change this time.

Analyst: (sample solution)
Date:

---

## Part 1: Classify the Change

Answer each question the same way `ChangeClassifier.java` did in Lab 2.1.

| Question | Yes / No | Why |
| :--- | :--- | :--- |
| Does it remove or rename any existing field? | No | It adds a brand-new endpoint; nothing existing is touched |
| Does it tighten validation on an existing endpoint? | No | `GreetingController` and `HealthController` are untouched |
| Does it turn an existing optional parameter into a required one? | No | No existing parameter is affected |
| Does it change the path or method of an existing endpoint? | No | `/api/v1/farewells/{name}` is a new path; no existing route changes |

## Part 2: Verdict

Non-breaking. None of the four rows fire — the same shape as Lab 2.1's `C5` ("add a new `GET .../formal` endpoint"), which the worksheet also classified as non-breaking, because a consumer who never calls a path cannot be broken by that path existing.

## Part 3: Version Placement

Ships in the current `v1`. Exactly like `C5` in Lab 2.1, a wholly new, additive endpoint carries no risk to any existing `v1` consumer — there is nothing here to make them keep using an old version for. It reuses the same base path prefix (`/api/v1/`) and the same response shape (a `Map` with a `message` field) as `GreetingController`, so it is consistent with the rest of `v1` rather than introducing a one-off convention.

## Part 4: One Risk to Watch

If a future change ever repurposed the `/api/v1/farewells/{name}` path itself — for example, changing its response field name from `message` to something else, or requiring a header that other `v1` endpoints don't require — that would be breaking under the same checklist and would need its own version bump, exactly like `C2` and `C3` from Lab 2.1.
