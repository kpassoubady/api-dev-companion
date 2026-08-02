# Breakout Exercise: Lab 2.3 — Simulating an Automated API Test Suite

Day 4, Session 2. Budget 30 minutes.

Lab 1.1 audited this API by hand: you sent requests, read the responses, and wrote down what you found. The test suite already in `quickstart-project` only proves the happy path — one passing request per controller. Today you turn Lab 1.1's manual findings into JUnit5 assertions that run every time, the same shift-left move the DevOps overview just covered: unit tests as the first, fastest gate in a pipeline, run from IntelliJ standing in for what a Maven `verify` phase does for real.

## Objectives

By the end of this lab you will have:

1. Extended both real test classes from one happy-path test each to a small suite covering negative paths, method-not-allowed handling, and content-type contracts.
2. Implemented four TODOs, each one a direct assertion of something Lab 1.1's worksheet recorded by hand: the `Allow` header on a 405, the `Content-Type` on a 2xx, and how the API handles path-variable edge cases.
3. Run the suite from IntelliJ and from `mvn test`, and confirmed both agree.
4. (Stretch) Run the same API's Postman collection headlessly with Newman, so the same test intent — verify behavior — running in two different tools.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first.

Start the shared course API and leave it running (only needed for the Newman stretch goal — MockMvc tests below start their own embedded test context and do not need it):

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/HealthControllerTest.additions-template.java` | The real `HealthControllerTest.java`'s one existing test, plus a worked-example negative-path test, plus two `TODO`s. |
| `start/GreetingControllerTest.additions-template.java` | The real `GreetingControllerTest.java`'s one existing test, plus two `TODO`s. |
| `start/NEWMAN-STRETCH.md` | The exact command for the Newman stretch goal. |
| `solution/HealthControllerTest.java`, `solution/GreetingControllerTest.java` | Completed reference versions of the real files. Open after you have tried. |

## Instructions

### Step 1: Read the worked example (5 minutes)

Open `start/HealthControllerTest.additions-template.java`. Read `unmappedPathReturns404` — it is the worked example, written the same way `checkAuthentication` was in Lab 2.2: already implemented, already passing, and the shape the TODOs should follow. It turns Lab 1.1's manual observation ("an unmapped path comes back 404, not a blank 200") into an assertion.

### Step 2: Implement the TODOs (18 minutes)

Copy both template files' new test methods onto the real files at `api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/HealthControllerTest.java` and `GreetingControllerTest.java`. Do not create second copies of these classes; there must be exactly one of each, in the setup repo.

Implement, in any order:

1. `postToHealthReturns405WithAllowHeader` (TODO 1, in `HealthControllerTest`) — `POST /api/v1/health` returns `405`, and the `Allow` header contains `GET`.
2. `healthResponseContentTypeIsJson` (TODO 2, in `HealthControllerTest`) — `GET /api/v1/health` returns `Content-Type: application/json`.
3. `greetingHandlesNameWithSpaces` (TODO 3, in `GreetingControllerTest`) — `GET /api/v1/greetings/{name}` with a name containing a space still succeeds and reflects the name unmodified.
4. `greetingWithoutNameReturns404` (TODO 4, in `GreetingControllerTest`) — `GET /api/v1/greetings/` with no name segment returns `404`, not a blank `200`.

Every one of these is a specific fact Lab 1.1's worksheet already recorded about this API; you are not guessing at behavior, you are making an existing observation repeatable.

### Step 3: Run it (7 minutes)

In IntelliJ, right-click each test class and choose **Run**, or run both classes together from the project root:

```bash
cd ../api-dev-setup/quickstart-project
mvn test
```

You should see 4 tests pass in `HealthControllerTest` and 3 in `GreetingControllerTest`, seven total, all green. If any fail, re-read the `TODO` comment for that test; each one names the exact status code and header or JSON path to assert on.

## Stretch Goals

If you finish early:

1. Add more negative test cases beyond the four required TODOs. Lab 1.1's worksheet has more raw material than this lab used: try `HEAD /api/v1/health` (same status and content type as `GET`, empty body), or a greeting name that is a number instead of a word.
2. Run the existing Postman collection headlessly with Newman, simulating the "deploy & test" stage of a CI pipeline instead of clicking through Postman by hand. See `start/NEWMAN-STRETCH.md` for the exact command and prerequisites. Run it with your terminal in this lab's own folder (`day4/lab2_3_simulating_an_automated_api_test_suite/`), not the companion repo root used elsewhere in this README:

   ```bash
   npx newman run ../../../api-dev-setup/quickstart-project/postman/api-dev-quickstart.postman_collection.json
   ```

   Alternatively, if you don't have Node/npm installed to run Newman, you can use the provided `start/curl-tests.sh` script to simulate a pure shell-based test suite validation instead:

   ```bash
   # Mac/Linux or Git Bash
   ./start/curl-tests.sh

   # Windows PowerShell
   .\start\curl-tests.ps1
   ```

   This project has no database, so there is nothing here for Testcontainers to spin up — that tool solves a different problem (real dependencies for integration tests) than this lab's unit-level MockMvc suite or Newman's black-box collection run. If you want to see the containerized version of Newman instead of running it via `npx`, you are welcome to write your own `docker-compose.yml` for it, but that is optional; the `npx newman run` command above is the complete stretch goal.

## If You Get Stuck

`solution/` has completed, verified versions of both real test files (`mvn test` passes seven tests, zero failures). Compare your `TODO` implementations against them one at a time rather than replacing your whole file — the value is in seeing where your assertion differs, not in copying the answer.

## What This Feeds

Lab 2.4, this afternoon's capstone, integrates contract design, documentation, versioning, security, and testing from all four days into one exercise. The test suite you just built is the testing piece; you will be extending it further, not starting over.
