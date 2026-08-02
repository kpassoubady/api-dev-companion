#!/bin/bash
# Lab 2.4: Capstone Exercise (cURL tests)

echo "--- 1. GET /api/v1/farewells/Learner ---"
curl -s -i -X GET http://localhost:8080/api/v1/farewells/Learner
echo -e "\n\n"

echo "--- 2. GET /api/v1/greetings/Learner ---"
curl -s -i -X GET http://localhost:8080/api/v1/greetings/Learner
echo -e "\n\n"

echo "--- 3. GET /api-docs (Check for FarewellController in spec) ---"
curl -s -i -X GET http://localhost:8080/api-docs
echo -e "\n"
