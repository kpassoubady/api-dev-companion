# Breakout Exercise: Lab 1.1 — Analyzing an Existing API

Day 1, Session 4. Budget 30 minutes.

You have spent the session learning what REST conventions are and why generic infrastructure depends on them. This lab points that checklist at a real, running API and asks you to produce evidence rather than opinions.

## Objectives

By the end of this lab you will have:

1. Identified an API's protocol family from observed behaviour, not from its documentation.
2. Scored it against the eight resource-naming rules, the method guarantees, and the RFC 9457 error standard.
3. Implemented the convention checks in code, so the scoring is repeatable instead of a one-time reading.
4. Decided, for each finding, whether the deviation is defensible and written down why.

That last objective is the real one. Any linter can flag a trailing slash. Judging which findings matter is the skill this course is teaching.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first. You need a JDK (17 or newer, 21 recommended) and Postman.

Start the shared course API and leave it running:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

Confirm it answers:

```bash
curl http://localhost:8080/api/v1/health
```

If port 8080 is taken, see [Free Up Port 8080](../../../api-dev-setup/quickstart-project/README.md#free-up-port-8080) in the setup repo.

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/API-ANALYSIS-WORKSHEET.md` | The worksheet you fill in. Nine parts, each needing evidence. |
| `start/RestConventionChecker.java` | The checker. Rule 1 is implemented as a worked example; four TODOs are yours. |
| `start/curl-tests.sh` | Equivalent cURL commands for terminal users. |
| `solution/` | A completed worksheet and a finished checker. Open it after you have tried. |

## Instructions

### Step 1: Observe before you judge (10 minutes)

Import the collection at `api-dev-setup/quickstart-project/postman/api-dev-quickstart.postman_collection.json` into Postman, or run the provided `start/curl-tests.sh` script (Mac/Linux or Git Bash) or `start\curl-tests.ps1` (Windows PowerShell) if you prefer the terminal. Send these requests and record what actually comes back in Part 3 of the worksheet:

> **Note**: If you are short on time, a pre-made Postman collection containing *all* the requests needed for this audit (including the ones missing from the setup collection) is available at `solution/api-dev-lab1-audit.postman_collection.json`.

| Request | What to look for |
| :--- | :--- |
| `GET /api/v1/health` | Status, `Content-Type`, body shape |
| `GET /api/v1/greetings/YourName` | Is the identifier in the path or the query? |
| `POST /api/v1/health` | The status, and whether an `Allow` header comes back |
| `GET /api/v1/accounts/999` | The error body. Read every field. |
| `GET /api-docs` | The declared `info.version`. Compare it to the paths. |
| `GET /swagger-ui.html` | The status code may surprise you |

Send `GET /api/v1/health` three times and compare the bodies. That is how you test idempotence rather than assume it.

### Step 2: Implement the checks (12 minutes)

Open `start/RestConventionChecker.java`. Read `checkNoVerbsInPath` first: it is the worked example and the four TODOs follow the same shape.

```bash
mvn compile
mvn exec:java -pl day1/lab1_1_analyzing_an_existing_api/start -Dexec.mainClass="com.kavinschool.api.RestConventionChecker"
```

It runs immediately and reports a 100% adherence score. That score is a lie, and making it honest is your job.

| TODO | Rule | Watch out for |
| :--- | :--- | :--- |
| 1 | Rule 2, plural collections | `health` is uncountable; do not flag it |
| 2 | Rules 4 and 6, naming style | Skip identifier segments. `/greetings/Ada` is data, not camelCase |
| 3 | Rule 7, explicit versioning | Two endpoints will fail this, and both are arguably fine |
| 4 | RFC 9457 error shape | The stack-trace check finds a genuine defect |

Then complete TODO 5: add the endpoints you observed in Step 1 to the `OBSERVATIONS` list.

To prove a check works rather than assuming it, add a deliberately bad observation such as `GET /api/v1/order/42` and confirm your rule 2 check fires. Remove it afterwards.

### Step 3: Fill in the judgement (8 minutes)

Complete Parts 4 through 8 of the worksheet. Part 8 is the deliverable. Answer three questions in prose:

1. Which rule did this API break most often?
2. Which deviation would you defend, and on what grounds?
3. Which single change would most improve it for a consumer?

Bring your answers to the discussion block. We will compare verdicts, and disagreement is the useful outcome.

## Stretch Goals

If you finish early:

1. Score a second API against the same checklist. `https://api.github.com` needs no authentication for public repositories: try `GET https://api.github.com/repos/spring-projects/spring-boot`, then a repository that does not exist, and compare the error body to the quickstart API's.
2. Document where the second API deviates from REST conventions and whether it is a defect or a deliberate trade-off. Its versioning approach is the interesting case.
3. Add a severity weighting to the checker so the score reflects `HIGH` findings more than `LOW` ones, and note whether the ranking changes your verdict.

## If You Get Stuck

`solution/` has a finished checker and a completed worksheet with the reasoning written out. Read the worksheet's Part 8 even if you finish on your own: the argument about which findings are defensible is the part most worth comparing against.

Two hints before you look:

- If every endpoint still passes after implementing a TODO, your check is probably returning the empty list it was given. Add a `System.out.println` inside the loop and confirm it runs.
- If `/greetings/Ada` fails rule 4, you are checking the whole path string instead of the segments. Use `segmentsOf(path)` and skip `isIdentifier(segment)`.

## What This Feeds

Day 2 asks you to write an OpenAPI specification. The deviations you find today are the list of things your own contract should get right, and the version mismatch in `/api-docs` is the first item on it. Keep the worksheet.
