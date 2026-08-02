Write-Host "--- 1. GET /api-docs (OpenAPI JSON Specification) ---"
curl.exe -s -i -X GET http://localhost:8080/api-docs
Write-Host "`n`n"

Write-Host "--- 2. GET /swagger-ui.html (Swagger UI HTML) ---"
curl.exe -s -i -X GET http://localhost:8080/swagger-ui.html
Write-Host "`n"
