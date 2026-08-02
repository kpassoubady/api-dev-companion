# Breakout Exercise: Lab 2.4 — Capstone Exercise

Day 4, Session 4, Breakout 2. Budget 40 minutes.

This is the last lab of the course, and it does not teach anything new. Over the last three and a half days you designed a contract (Lab 1.2), documented real controllers (Lab 1.3), classified changes as breaking or non-breaking (Lab 2.1), gated a request through authentication, authorization, and rate limiting (Lab 2.2), and wrote an automated test suite (Lab 2.3). This lab asks you to run that same sequence — design, document, secure, test — against one small addition of your own: a new `FarewellController`, parallel in shape to the `GreetingController` you have been working with all week.

It is heavily scaffolded on purpose. Forty minutes is not enough time to build this from scratch and it isn't supposed to be. Every file below is already mostly written; you are filling in the specific TODOs that correspond to each of the four skills, in the order you learned them.

## Objectives

By the end of this lab you will have:

1. Classified `GET /api/v1/farewells/{name}` as additive/non-breaking using Lab 2.1's checklist, and decided it belongs in the current `v1`.
2. Added springdoc-openapi annotations to a new controller, the same pattern Lab 1.3 applied to `GreetingController`.
3. Filled in one small authorization + rate-limit check reusing the shape of Lab 2.2's `ApiSecurityGate`.
4. Written two JUnit5 tests — one happy path, one negative case — following the MockMvc pattern already established for `GreetingControllerTest` and `HealthControllerTest`.

None of these four things is new. Recognizing which lab's pattern to reach for, and applying it correctly under a tight clock, is the actual capstone.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first.

Start the shared course API and leave it running, so you can verify Step 2's documentation and run Step 4's tests against a real Maven project:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

Confirm it still answers as expected before you add anything:

```bash
curl http://localhost:8080/api/v1/greetings/Learner
```

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/CAPSTONE-CHECKLIST.md` | The design/versioning worksheet. Four short TODOs. |
| `start/FarewellController.template.java` | Reference template for a **new** file in the setup repo. One TODO block: the OpenAPI annotations. |
| `start/FarewellSecurityGate.java` | Standalone, runnable security simulation, scaled down from Lab 2.2. One TODO: the combined authorization + rate-limit check. |
| `start/FarewellControllerTest.template.java` | Reference template for a **new** test file in the setup repo. Two TODOs: one happy-path test, one negative test. |
| `solution/` | Completed versions of all four files above. Open it after you have tried. |

## Instructions

### Step 1: Design & Versioning (8 minutes)

Open `start/CAPSTONE-CHECKLIST.md`. It reuses Lab 2.1's four breaking/non-breaking questions, pointed at the new endpoint instead of someone else's proposed change. Fill in all four TODOs: the classification table, the verdict, the version placement, and one risk that would flip the verdict if you got it wrong.

You do not need any code running for this step — the same was true of Lab 2.1's worksheet.

### Step 2: Documentation (8 minutes)

Open `start/FarewellController.template.java`. It is a complete, working controller already — the mapping, the path variable, and the response body are all correct. The one TODO block is the springdoc-openapi annotations, following exactly what you did to `GreetingController` in Lab 1.3.

Fill in the TODO, then copy the whole file to:

```
api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/FarewellController.java
```

Let Spring Boot's dev tools reload (or restart `mvn spring-boot:run`), then check `http://localhost:8080/swagger-ui.html`. Your new endpoint should appear with the summary and description you wrote.

### Step 3: Security (8 minutes)

Open `start/FarewellSecurityGate.java`. `checkAuthentication` is carried over unchanged from Lab 2.2 as a worked example. The one TODO, `checkFarewellAccess`, combines that lab's two separate checks — authorization and rate limiting — into a single scaled-down method, matching the size of this endpoint. This file is self-contained; it does not touch the running API.

```bash
cd start
java FarewellSecurityGate.java
```

Confirm `F3` is rejected with `403` (wrong role) and `F4` with `429` (over the limit), and that `F2` never reaches your new check at all — it fails authentication first, the same short-circuit behavior Lab 2.2's gate demonstrated.

### Step 4: Testing (16 minutes)

Open `start/FarewellControllerTest.template.java`. It follows the same `@SpringBootTest` + `MockMvc` pattern as the existing `GreetingControllerTest.java` and `HealthControllerTest.java` in the setup repo. Fill in the two TODOs:

1. A happy-path test: `GET /api/v1/farewells/Learner` returns `200` with the expected `message`.
2. A negative test: `POST` to the same path returns `405` — the unsupported-method check Lab 1.1 first asked you to make by hand, now a repeatable assertion.

Copy the completed file to:

```
api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/FarewellControllerTest.java
```

Then run the suite:

```bash
cd ../api-dev-setup/quickstart-project
mvn test
```

Both new tests should pass alongside the existing `GreetingControllerTest` and `HealthControllerTest`.

## Stretch Goals

If you finish early:

1. Add a third test: an empty or blank `{name}` path segment. Observe what Spring actually returns and decide whether it is worth asserting on, or worth a validation change — and if it's a validation change, re-run it through `CAPSTONE-CHECKLIST.md`'s Part 1 first.
2. Add a `404` response example to `FarewellController`'s OpenAPI annotations for a name that doesn't exist in some hypothetical backing store, even though the current implementation always returns `200`.
3. Add a third check to `FarewellSecurityGate`, `checkAudienceClaim` (same idea as Lab 2.2's stretch goal), and decide where in the order it belongs relative to `checkFarewellAccess`.

## If You Get Stuck

`solution/` has all four completed files, including the reasoning written out in `CAPSTONE-CHECKLIST.md`. Two hints before you look:

- If `swagger-ui.html` doesn't show your new endpoint after Step 2, confirm the file actually landed in the `controller` package in the setup repo, not just in this companion repo — annotating the template here does nothing on its own.
- If the negative test in Step 4 fails with `200` instead of `405`, you likely sent a `GET` instead of a `POST` — check the import and the method call, not the controller.

## Course Closing

Day 1 opened by asking you to point a REST-convention checklist at someone else's API and decide, endpoint by endpoint, which deviations actually mattered. Every lab since then has been a variation on that same judgment call, aimed at a different part of the API lifecycle: what belongs in a contract, what belongs in a new version, what a request has to prove before it's served, what a test has to prove before you trust it. Today you pointed all four at your own code for the first time. The tools will keep changing — the framework, the spec format, the auth scheme — but the habit underneath all of them, evidence over assumption, is the one worth keeping.
