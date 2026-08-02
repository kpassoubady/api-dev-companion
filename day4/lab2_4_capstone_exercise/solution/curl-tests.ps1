Write-Host "--- 1. GET /api/v1/farewells/Learner ---"
curl.exe -s -i -X GET http://localhost:8080/api/v1/farewells/Learner
Write-Host "`n`n"

Write-Host "--- 2. GET /api/v1/greetings/Learner ---"
curl.exe -s -i -X GET http://localhost:8080/api/v1/greetings/Learner
Write-Host "`n`n"

Write-Host "--- 3. GET /api-docs (Check for FarewellController in spec) ---"
curl.exe -s -i -X GET http://localhost:8080/api-docs
Write-Host "`n"
