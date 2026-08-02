# Breakout Exercise: Lab 2.1 — Designing a Non-Breaking Version Strategy

Day 3, Session 1. Budget 30 minutes.

Yesterday you wrote and reviewed a contract for the shared quickstart API. Today that contract has to survive change. You will classify a set of proposed changes as breaking or non-breaking, then design a version strategy that lets the API evolve without breaking Day 2's Lab 1.2/1.3 consumers.

## Objectives

By the end of this lab you will have:

1. Implemented the breaking/non-breaking checklist as repeatable code, not a one-time judgment call.
2. Classified five proposed changes to the shared API, plus two of your own.
3. Chosen and justified a versioning strategy for the API's next version.
4. Written a deprecation plan for the version being replaced.

That last objective is the real one. Anyone can bump a version number. Deciding when to bump it, and how to retire the old one, is the skill this session is teaching.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first.

The shared quickstart API does not need to be running for this lab — the exercise works against the endpoints' documented shape, from `GreetingController` and `HealthController`, which you can read directly:

```
../../../api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/
```

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/ChangeClassifier.java` | The classifier you complete. The field-rename check is done as a worked example; three `TODO`s are yours. |
| `start/VERSION-STRATEGY-WORKSHEET.md` | The worksheet you fill in, using the classifier's output. |
| `solution/` | A completed classifier and worksheet. Open it after you have tried. |

## Instructions

### Step 1: Read the worked example (5 minutes)

Open `start/ChangeClassifier.java`. Read `checkFieldRename` first: it is the worked example, and the three TODOs follow the same shape — check one condition, return a `Finding` explaining the consumer-facing consequence if it's true, otherwise return `null`.

### Step 2: Implement the checks (12 minutes)

```bash
mvn compile
mvn exec:java -pl day3/lab2_1_designing_a_non_breaking_version_strategy/start -Dexec.mainClass="com.kavinschool.api.ChangeClassifier"
```

It runs immediately and reports `C2`, `C3`, and `C4` as non-breaking. That's wrong; making it honest is your job. Implement:

1. `checkValidationTightening` (TODO 1)
2. `checkRequiredParamAdded` (TODO 2)
3. `checkPathOrMethodChanged` (TODO 3)

Then complete TODO 4: add two proposed changes of your own, based on a real field in `GreetingController` or `HealthController`.

### Step 3: Fill in the worksheet (13 minutes)

Complete Parts 1 through 5 of `VERSION-STRATEGY-WORKSHEET.md`, using your classifier's output to fill in Part 2.

> Stretch: draft a deprecation/sunset notice for `v1`; describe how you'd ship one of your breaking changes (C6/C7) as non-breaking instead, if possible.

## If You Get Stuck

`solution/` has a finished classifier and a completed worksheet with the reasoning written out. Read the worksheet's Part 5 even if you finish on your own — the justification for the chosen strategy is the part most worth comparing against.

## What This Feeds

This session's second half adds security rules to the same API. Bring your version strategy: Lab 2.2 asks where security-relevant fields (like an auth header requirement) belong in the versioning decisions you just made.
