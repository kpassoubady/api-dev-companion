# api-dev-companion

Exercise companion repository for the **Fundamentals of API Development** course.

This repo contains the hands-on breakout exercises, starter code, and reference solutions. Each exercise has a `start/` directory you work in and a `solution/` directory to check yourself against.

## Prerequisites

Complete the installation guide in the [api-dev-setup](https://github.com/kpassoubady/api-dev-setup) repo before Day 1. You need:

| Tool | Version | Why |
| :--- | :--- | :--- |
| **JDK** | 17 or newer, 21 recommended | Runs the exercise code |
| **Maven** | 3.9+ | Builds the shared Spring Boot API |
| **IntelliJ IDEA** | Community or Ultimate | The course IDE |
| **Postman** | Latest | Exploring and testing endpoints |
| **Git** | Any recent version | Cloning these repos |

Verify your setup with the [quickstart project](https://github.com/kpassoubady/api-dev-setup/tree/main/quickstart-project) before class rather than during it.

## Repository Layout

Clone all three repos as siblings, because the exercises reference the shared API by relative path:

```text
your-workspace/
├── api-dev/               # slides, diagrams, instructor demos
├── api-dev-setup/         # install guides + the shared Spring Boot API
└── api-dev-companion/     # this repo: concept docs + breakout exercises
    └── day1/
        ├── concepts/
        │   └── api-purpose-and-types.md
        └── lab1_1_analyzing_an_existing_api/
            ├── README.md
            ├── start/
            └── solution/
```

Each day's `concepts/` folder holds the reference docs for that day — read them before or alongside the labs.

## Starting the Shared API

Most exercises analyse or extend one Spring Boot REST API, which lives in the setup repo and is extended lab by lab across the four days:

```bash
cd ../api-dev-setup/quickstart-project
mvn spring-boot:run
```

It listens on port 8080. Leave it running for the whole session:

```bash
curl http://localhost:8080/api/v1/health
# {"status":"UP","service":"api-dev-quickstart"}
```

Swagger UI is at <http://localhost:8080/swagger-ui.html>. If port 8080 is taken, see [Free Up Port 8080](https://github.com/kpassoubady/api-dev-setup/tree/main/quickstart-project#free-up-port-8080).

## Running Exercise Code

Standalone exercise files need no build step. The JDK single-file source launcher compiles and runs them in one command:

```bash
cd day1/lab1_1_analyzing_an_existing_api/start
java RestConventionChecker.java
```

If your default JDK is newer than 21 and you want to confirm the code compiles at the course's language level, add `--source 21`.

Exercises that ship as a Maven project instead include their own `pom.xml` and `README.md` with the commands to use.

## Exercises

### Day 1: API Purpose, Types & Protocols

[Concept docs](day1/concepts/): API purpose and types, protocols (REST/SOAP/RPC), RESTful standards.

| Lab | Title | What you produce |
| :--- | :--- | :--- |
| [1.1](day1/lab1_1_analyzing_an_existing_api/) | Analyzing an Existing API | A protocol and standards analysis of a real API, with a working convention checker |

### Day 2: API Contracts, Documentation & Design

[Concept docs](day2/concepts/): API contracts & OpenAPI/Swagger, contract-first vs. code-first, documenting APIs.

| Lab | Title | What you produce |
| :--- | :--- | :--- |
| [1.2](day2/lab1_2_writing_an_openapi_specification/) | Writing an OpenAPI Specification | A hand-written OpenAPI 3.1 spec for the quickstart API's health and greeting endpoints |
| [1.3](day2/lab1_3_peer_review_and_interactive_docs/) | Peer Review & Interactive Docs | A peer review of a partner's spec, plus springdoc-openapi annotations added to the real controllers |

Days 3 and 4 are added as the course progresses.

## How to Work Through an Exercise

1. Read the exercise `README.md` first. It states the time budget and the deliverable.
2. Work in `start/`. Every place you need to write code is marked with a numbered `TODO` explaining what to implement.
3. Run your code often. The starters are written to compile and run from the first minute, so you always have something to execute.
4. Check `solution/` when you are done, or when you are stuck and have tried. The solutions include the reasoning, not just the code.

## Related Repositories

- [api-dev](https://github.com/kpassoubady/api-dev): Course catalog, outlines, slides, diagrams, and instructor demos.
- [api-dev-setup](https://github.com/kpassoubady/api-dev-setup): Installation guides and the environment verification quickstart project.
