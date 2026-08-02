Write-Host "--- 1. GET /api/v1/health ---"
curl.exe -s -i -X GET http://localhost:8080/api/v1/health
Write-Host "`n`n"

Write-Host "--- 2. GET /api/v1/greetings/YourName ---"
curl.exe -s -i -X GET http://localhost:8080/api/v1/greetings/YourName
Write-Host "`n`n"

Write-Host "--- 3. POST /api/v1/health ---"
curl.exe -s -i -X POST http://localhost:8080/api/v1/health
Write-Host "`n`n"

Write-Host "--- 4. GET /api/v1/accounts/999 (Expect Error) ---"
curl.exe -s -i -X GET http://localhost:8080/api/v1/accounts/999
Write-Host "`n`n"

Write-Host "--- 5. GET /api-docs ---"
curl.exe -s -i -X GET http://localhost:8080/api-docs
Write-Host "`n`n"

Write-Host "--- 6. GET /swagger-ui.html ---"
curl.exe -s -i -X GET http://localhost:8080/swagger-ui.html
Write-Host "`n"
