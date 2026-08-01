// Reference template for annotating the REAL file at:
//   api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/HealthController.java
//
// Copy the imports and annotations below onto that file. Do not create a
// second HealthController; there must be exactly one, in the setup repo.

package com.apidev.quickstart.controller;

// TODO 1: import io.swagger.v3.oas.annotations.Operation;
// TODO 2: import io.swagger.v3.oas.annotations.media.Content;
// TODO 3: import io.swagger.v3.oas.annotations.media.ExampleObject;
// TODO 4: import io.swagger.v3.oas.annotations.media.Schema;
// TODO 5: import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // TODO 6: Add @Operation(summary = "...", description = "...") above the
    //         method, matching what you wrote in Lab 1.2's openapi-quickstart.yaml.

    // TODO 7: Add @ApiResponse(responseCode = "200", description = "...",
    //         content = @Content(schema = @Schema(implementation = Map.class),
    //         examples = @ExampleObject(value = "..."))) above the method too.

    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", "api-dev-quickstart"
        );
    }
}
