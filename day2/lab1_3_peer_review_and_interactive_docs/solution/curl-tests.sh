#!/bin/bash
# Lab 1.3: Peer Review and Interactive Docs (cURL tests)

echo "--- 1. GET /api-docs (OpenAPI JSON Specification) ---"
curl -s -i -X GET http://localhost:8080/api-docs
echo -e "\n\n"

echo "--- 2. GET /swagger-ui.html (Swagger UI HTML) ---"
curl -s -i -X GET http://localhost:8080/swagger-ui.html
echo -e "\n"
