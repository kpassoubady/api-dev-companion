# Breakout Exercise: Lab 1.0 — Classifying API Audience Types

Day 1, Session 2. Budget 15-20 minutes.

You have just seen the DemoAudienceProjections demo show one resource projected three ways for Open, Partner, and Internal audiences. This exercise asks you to apply that lens to the quickstart API itself: classify each endpoint by who should call it, decide what data each audience should see, and identify what leaks when you skip the projection.

## Objectives

By the end of this exercise you will have:

1. Classified every endpoint in the quickstart API by audience type (Open, Partner, Internal).
2. Identified which response fields are safe for each audience and which are not.
3. Flagged at least one field that would leak if the endpoint were served as-is to a wider audience.
4. Counted how many endpoints you discovered that are not in the provided Postman collection, as a first taste of sprawl.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first. You need Postman.

Start the shared course API and leave it running:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

Confirm it answers:

```bash
curl http://localhost:8080/api/v1/health
```

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/AUDIENCE-WORKSHEET.md` | The worksheet you fill in. Four parts, each needing evidence. |
| `solution/AUDIENCE-WORKSHEET.md` | A completed worksheet. Open it after you have tried. |

## Instructions

### Step 1: Inventory every endpoint you can find (5 minutes)

Import the collection at `api-dev-setup/quickstart-project/postman/api-dev-quickstart.postman_collection.json` into Postman. Send every request in the collection and record what comes back in Part 1 of the worksheet.

Then go beyond the collection. Try a few paths you suspect might exist: `/api/v1/users`, `/api/v1/accounts`, `/actuator/health`, `/error`. Record anything you find.

Count how many endpoints you discovered that were NOT in the provided collection. That number is your first sprawl metric.

### Step 2: Classify each endpoint by audience (5 minutes)

For each endpoint you found, decide which audience type it belongs to. Use the definitions from the session:

| Type | Who calls it | Auth posture |
| :--- | :--- | :--- |
| Open | Anyone who signs up | API key, public docs, deprecation policy |
| Partner | Named orgs under contract | OAuth client credentials or mTLS, SLA |
| Internal | Teams inside the org | Service identity behind the gateway |

Fill in Part 2. For each classification, write one sentence justifying it.

### Step 3: Identify what leaks (5 minutes)

Pick one endpoint you classified as Internal. In Part 3, list the response fields it returns. Mark which ones would be safe to expose to an Open audience, which would be safe for a Partner audience, and which must stay Internal.

If the quickstart API has no sensitive fields, describe what a real Internal endpoint *would* expose (think: internal hostnames, database shard keys, analyst email addresses, stack traces).

### Step 4: Answer the sprawl question (remaining time)

Complete Part 4. Write two or three sentences answering: if this API grew to 200 endpoints over two years with no catalog, what would break first?

## Stretch Goal

Pick a public API (suggestion: `https://api.github.com`). Send `GET /rate_limit` and `GET /user` (authenticated with a personal access token). Classify each endpoint by audience type. Which one requires authentication? Which one leaks nothing without it? Write your answer in the stretch section of the worksheet.

## If You Get Stuck

`solution/` has a completed worksheet. Read the Part 4 answer even if you finish on your own: the sprawl argument is the part most worth comparing against.

## What This Feeds

Lab 1.1 later today asks you to score the quickstart API against REST conventions. The endpoints you discovered here that were not in the collection are the ones you will add to the checker's observation list. Keep this worksheet.
