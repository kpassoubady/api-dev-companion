# Breakout Exercise: Lab 1.3 — Peer Review & Interactive Docs

Day 2, Session 2. Budget 30 minutes.

Lab 1.2 gave you a contract-first spec. This lab does two things with it: a peer review, the same discipline as a code review but pointed at a `.yaml` file, and a side-by-side comparison against the code-first version springdoc-openapi generates once the real controllers carry annotations.

## Objectives

By the end of this lab you will have:

1. Scored a partner's Lab 1.2 spec against a completeness checklist, not just "does it parse."
2. Added springdoc-openapi annotations to the quickstart API's real controllers and watched the generated spec change immediately.
3. Compared a hand-written contract-first spec against a generated code-first one for the same two endpoints.
4. Recorded any place the two disagreed, and decided which one was actually correct.

## Setup

Follow the environment setup in the [companion repository root README](../../README.md) first. You need Lab 1.2's completed `openapi-quickstart.yaml`, either yours or a partner's.

Start the shared course API and leave it running:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

Confirm the pre-lab docs are being served (unannotated, so springdoc has almost nothing to say yet). You can run the provided `start/curl-tests.sh` script to fetch the JSON spec and verify the Swagger UI endpoint is reachable:

```bash
# Mac/Linux or Git Bash
./start/curl-tests.sh

# Windows PowerShell
.\start\curl-tests.ps1
```

Alternatively, you can open `http://localhost:8080/swagger-ui.html` in a browser and keep it open. You will reload it after Step 2.

## What's in Here

| Path | What it is |
| :--- | :--- |
| `start/PEER-REVIEW-CHECKLIST.md` | The checklist you fill in against a partner's Lab 1.2 spec. |
| `start/HealthController.annotated-template.java` | `HealthController.java` with the springdoc annotations marked as `TODO`. Copy your finished version over the real file in `api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/HealthController.java`. |
| `start/GreetingController.annotated-template.java` | Same idea, for `GreetingController.java`. |
| `solution/PEER-REVIEW-CHECKLIST.md` | A filled-in example review. |
| `solution/HealthController.java`, `solution/GreetingController.java` | The fully annotated reference controllers. |

## Instructions

### Step 1: Peer review (10 minutes)

Trade `openapi-quickstart.yaml` files with a partner. Fill in `start/PEER-REVIEW-CHECKLIST.md` against theirs: does every endpoint have a description, does every schema field have a description, is the response actually documented and not just assumed. Write down anything you would ask them to fix, with a reason.

### Step 2: Annotate the real controllers (12 minutes)

Open `api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/HealthController.java` and `GreetingController.java`. Using the `TODO`s in `start/HealthController.annotated-template.java` and `start/GreetingController.annotated-template.java` as your guide, add `@Operation`, `@ApiResponse`, and `@Parameter` annotations directly to the real files.

Save, let Spring Boot's dev tools reload (or restart `mvn spring-boot:run`), then refresh `http://localhost:8080/swagger-ui.html`. Your summaries and descriptions should now appear in the live console, generated from the code you just annotated.

### Step 3: Compare the two (8 minutes)

Open your Lab 1.2 spec and the live `http://localhost:8080/api-docs` output side by side.

- Does the hand-written spec's `description` match what you actually wrote in the annotation?
- Does the generated spec have anything your hand-written one is missing, or the reverse?
- If they disagree about what a field means, which one would you trust, and why?

> Stretch: annotate a case the current controllers do not cover (an unmapped path returns a generic Spring error). Describe what `/swagger-ui.html` shows for a route that doesn't exist, and note whether it's worth documenting at all.

## Next

Bring your annotated controllers and both specs to Session 3's contract-first vs code-first discussion. You now have one artifact built each way for the exact same two endpoints.
