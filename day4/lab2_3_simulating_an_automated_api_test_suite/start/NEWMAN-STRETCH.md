# Stretch: Run the Postman Collection Headlessly with Newman

This simulates the "deploy & test" gate of a CI pipeline: the same Postman
collection you used by hand in Lab 1.1, run from the command line with no
browser and no clicking, the way a pipeline stage would run it.

## Prerequisites

- The shared quickstart API running: `cd ../../../api-dev-setup/quickstart-project && mvn spring-boot:run`
- Node.js installed (any recent LTS). You do not need to install Newman
  globally — `npx` downloads and runs it for you on demand.

## Run it

From this lab's own directory (`day4/lab2_3_simulating_an_automated_api_test_suite/`), with the
API still running in another terminal:

```bash
npx newman run ../../../api-dev-setup/quickstart-project/postman/api-dev-quickstart.postman_collection.json
```

The path has three `../` segments because it walks up from this lab folder
to the companion repo root, then up one more level to the sibling
`api-dev-setup` repo, before descending into `quickstart-project/postman/`.
If your clone layout differs, adjust the path or pass an absolute one.

Newman prints a pass/fail summary for every request and test script in the
collection (Health Check, Greeting, OpenAPI Docs), and exits non-zero if any
assertion fails — that non-zero exit code is exactly what a CI pipeline
stage checks to decide whether to gate a deployment.

## Optional: containerized version

If you want to see this run the way a CI runner would, with Newman itself
pinned to a container image instead of whatever Node you have locally, you
can write your own `docker-compose.yml` that mounts this collection into a
`postman/newman` image and runs the same command. This is not required —
`npx newman run ...` above is the complete stretch goal. Treat the
Docker version as an extra, only if you already have Docker running and
want to compare the two.
