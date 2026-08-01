#!/bin/bash
# Lab 1.1: Analyzing an Existing API (cURL equivalent to Postman collection)

echo "--- 1. GET /api/v1/health ---"
curl -s -i -X GET http://localhost:8080/api/v1/health
echo -e "\n\n"

echo "--- 2. GET /api/v1/greetings/YourName ---"
curl -s -i -X GET http://localhost:8080/api/v1/greetings/YourName
echo -e "\n\n"

echo "--- 3. POST /api/v1/health ---"
curl -s -i -X POST http://localhost:8080/api/v1/health
echo -e "\n\n"

echo "--- 4. GET /api/v1/accounts/999 (Expect Error) ---"
curl -s -i -X GET http://localhost:8080/api/v1/accounts/999
echo -e "\n\n"

echo "--- 5. GET /api-docs ---"
curl -s -i -X GET http://localhost:8080/api-docs
echo -e "\n\n"

echo "--- 6. GET /swagger-ui.html ---"
curl -s -i -X GET http://localhost:8080/swagger-ui.html
echo -e "\n"
