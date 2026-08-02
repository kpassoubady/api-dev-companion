# Breakout Exercise: Lab 2.2 — Implementing Basic API Security Rules

Day 3, Session 2. Budget 30 minutes.

This session covered three questions every request has to answer: who are you, what can you do, and how often can you do it. Today you implement the gate that answers all three, in the order production security middleware actually runs them: authentication first, then authorization, then rate limiting.

## Objectives

By the end of this lab you will have:

1. Implemented a role-based authorization check (RBAC), the same model Spring Security's `@PreAuthorize` uses.
2. Implemented a rate limit check based on a request count per window.
3. Seen, in your own output, why the check order matters: a request that fails authentication never gets evaluated for authorization or rate limiting.
4. Written two requests of your own that exercise a check the given data doesn't.

That third objective is the real one. The three checks are individually simple; the discipline is making sure they always run in the same order, every time, with no way for a later check to run before an earlier one has passed.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first.

This lab is fully self-contained and does not need the shared quickstart API running. Full OAuth 2.1 and a real Bucket4j-backed limiter are out of scope for a 30-minute lab — the tokens and clock here are simulated, standing in for what a real Spring Security filter chain and a Bucket4j filter would do. That's the same simplification the session's demos used.

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/ApiSecurityGate.java` | The gate you complete. The authentication check is done as a worked example; two `TODO`s are yours. |
| `solution/` | A completed gate with two additional requests. Open it after you have tried. |

## Instructions

### Step 1: Read the worked example (5 minutes)

Open `start/ApiSecurityGate.java`. Read `checkAuthentication` first: it is the worked example, and it runs before anything else in `gate()`. Notice it returns `null` on success and a `GateResult` only on failure — the two TODOs follow the same shape.

### Step 2: Implement the checks (15 minutes)

```bash
cd start
java ApiSecurityGate.java
```

It runs immediately, but every request that should fail authorization or rate limiting instead gets served. Implement:

1. `checkAuthorization` (TODO 1) — reject when `role` doesn't match `requiredRole`, with status `403`.
2. `checkRateLimit` (TODO 2) — reject when `requestsSoFarInWindow` exceeds `RATE_LIMIT_PER_WINDOW`, with status `429`.

Then complete TODO 3: add two requests of your own that exercise a different combination of pass/fail than `R1`-`R5`.

### Step 3: Trace the order (10 minutes)

Look at your output for `R2` (bad signature) and `R5` (over the rate limit, but a valid token). Answer in a comment at the bottom of the file, or out loud with your group:

1. Why does `R2` never get an authorization or rate-limit verdict?
2. What would go wrong if `checkRateLimit` ran before `checkAuthentication`?

> Stretch: add a fourth check, `checkAudienceClaim`, that rejects a request whose token wasn't issued for this specific API (add an `audience` field to `IncomingRequest` to support it), and decide where in the order it belongs.

## If You Get Stuck

`solution/` has a finished gate with two additional requests (`R6`, `R7`) exercising cases the starter data doesn't. Run it and compare which check stops each one.

## What This Feeds

Day 4 builds a JUnit5 test suite for the shared API (Lab 2.3). The same three-question shape — authentication, authorization, rate limiting — is exactly what that suite needs test cases for, alongside the endpoints you've been testing all week.
