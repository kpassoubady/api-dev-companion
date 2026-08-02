# Project Overview

This repository is the **student exercise companion** for the **Fundamentals of API Development** course. It contains hands-on breakout labs, concept reference docs, starter code, and reference solutions so students can practice API design and development concepts from each session.

## Directory Layout

- `day1/` through `day4/`: Content for each day of the course.
  - `concepts/`: Student-facing reference markdown docs for that day's topics.
  - `labX_*`: Individual breakout exercises. Each lab is a self-contained Maven module with:
    - `README.md`: Exercise instructions, time budget, and deliverables.
    - `start/`: Starter code and worksheets for students.
    - `solution/`: Reference solutions for instructors and self-checking.
- `pom.xml`: Multi-module Maven parent that includes `day1` through `day4`.
- `convert.py` / `convert_relative.py`: Scripts used to convert or migrate exercise formats into the companion layout.
- `llm-context/`: Stores context for LLM agents, organized by workflow stage (`brainstorm/`, `done/`, `info/`, `issues/`, `learnings/`, `prompts/`, `roadmap/`, `todos/`).

## Related Repositories

- [api-dev](https://github.com/kpassoubady/api-dev): Main course catalog, session outlines, slide decks, diagrams, and instructor demos.
- [api-dev-setup](https://github.com/kpassoubady/api-dev-setup): Installation guides and the environment verification quickstart project.

## Verification Steps

1. Confirm the multi-module project compiles from the repository root:
   ```bash
   mvn -q clean compile
   ```
2. Start the shared Spring Boot API from `api-dev-setup`:
   ```bash
   cd ../api-dev-setup/quickstart-project
   mvn spring-boot:run
   ```
   Verify `GET http://localhost:8080/api/v1/health` returns `{"status":"UP", ...}`.
3. Run a lab from the companion root. For example, to run the Lab 1.1 starter:
   ```bash
   cd api-dev-companion
   mvn exec:java -pl day1/lab1_1_analyzing_an_existing_api/start -Dexec.mainClass="com.kavinschool.api.RestConventionChecker"
   ```
   Replace the module path and main class with the lab you are verifying.
4. Run the solution variant the same way and confirm it produces the expected output for the lab.
5. Review each `dayN/concepts/*.md` file to ensure it only covers topics already introduced in the matching `api-dev/dayN/slides/` sessions.
6. Check that every lab's `README.md` explains the deliverable, how to start the shared API, and where the `start/` and `solution/` directories are.

## Content Guidelines

- Keep each lab focused on a single, bounded deliverable described in its `README.md`.
- `start/` should compile and run out of the box, with numbered `TODO` markers where students make changes. Do not leave the starter broken.
- `solution/` should be a complete, instructor-ready reference that matches the `start/` structure exactly except for the implemented `TODO`s.
- Concept docs in `dayN/concepts/` are student-facing references, not slide transcripts. Keep them dense, accurate, and scoped to the day's learning outcomes.
- When an exercise extends the shared Spring Boot API, document the relative path to `api-dev-setup/quickstart-project` and do not duplicate the API source in this repo.
- Lab instructions should reference only tools and concepts introduced in the corresponding `api-dev` session or earlier days.
- Maintain the root `pom.xml` module list so every `dayN/` directory and lab subproject is included in a clean `mvn compile`.
