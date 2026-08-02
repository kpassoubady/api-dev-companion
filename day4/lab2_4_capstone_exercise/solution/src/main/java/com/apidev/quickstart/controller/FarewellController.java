// Reference solution: the REAL file at
//   api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/FarewellController.java
// after completing this lab. Verified to compile and to appear correctly in
// /api-docs and /swagger-ui.html against springdoc-openapi 2.5.0.

package com.apidev.quickstart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FarewellController {

    @Operation(
        summary = "Get a personalized farewell",
        description = "Returns a farewell message that includes the supplied name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "A farewell for the supplied name.",
        content = @Content(
            schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(value = "{\"message\":\"Goodbye, Learner! See you in the next session.\"}")
        )
    )
    @GetMapping("/api/v1/farewells/{name}")
    public Map<String, String> farewell(
        @Parameter(description = "The name to bid farewell to.", required = true)
        @PathVariable String name
    ) {
        return Map.of("message", "Goodbye, " + name + "! See you in the next session.");
    }
}
