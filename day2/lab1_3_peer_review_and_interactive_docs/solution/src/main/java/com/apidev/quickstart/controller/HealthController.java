// Reference solution: the REAL file at
//   api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/HealthController.java
// after completing Lab 1.3. Verified to compile and to appear correctly in
// /api-docs and /swagger-ui.html against springdoc-openapi 2.5.0.

package com.apidev.quickstart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Operation(
        summary = "Check service health",
        description = "Returns the current status of the quickstart service."
    )
    @ApiResponse(
        responseCode = "200",
        description = "The service is healthy.",
        content = @Content(
            schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(value = "{\"status\":\"UP\",\"service\":\"api-dev-quickstart\"}")
        )
    )
    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", "api-dev-quickstart"
        );
    }
}
