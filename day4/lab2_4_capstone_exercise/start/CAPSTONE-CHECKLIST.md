# Capstone Checklist: Design & Versioning

Fill this in for the new `GET /api/v1/farewells/{name}` endpoint you are adding to the shared quickstart API. This reuses Lab 2.1's breaking/non-breaking checklist — you are not re-learning the criteria, just applying them to your own change this time.

Analyst:
Date:

---

## Part 1: Classify the Change

Answer each question the same way `ChangeClassifier.java` did in Lab 2.1.

| Question | Yes / No | Why |
| :--- | :--- | :--- |
| Does it remove or rename any existing field? | | |
| Does it tighten validation on an existing endpoint? | | |
| Does it turn an existing optional parameter into a required one? | | |
| Does it change the path or method of an existing endpoint? | | |

TODO 1: Answer all four rows above for `FarewellController`.

## Part 2: Verdict

TODO 2: Based on Part 1, is this change breaking or non-breaking? One sentence, naming which row (if any) it triggers.

## Part 3: Version Placement

TODO 3: Does this endpoint ship in the current `v1`, or does it need a new version? Justify in one or two sentences, using the same reasoning Lab 2.1's worksheet applied to `C5` (a comparable case: a brand-new endpoint added alongside existing ones).

## Part 4: One Risk to Watch

TODO 4: Name one thing that WOULD make this change breaking if you got it wrong — for example, reusing an existing path, or requiring a header the way Lab 2.2's gate does. One sentence.
