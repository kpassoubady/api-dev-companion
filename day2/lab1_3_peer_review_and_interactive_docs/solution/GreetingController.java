// Reference solution: the REAL file at
//   api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/GreetingController.java
// after completing Lab 1.3. Verified to compile and to appear correctly in
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
public class GreetingController {

    @Operation(
        summary = "Get a personalized greeting",
        description = "Returns a greeting message that includes the supplied name."
    )
    @ApiResponse(
        responseCode = "200",
        description = "A greeting for the supplied name.",
        content = @Content(
            schema = @Schema(implementation = Map.class),
            examples = @ExampleObject(value = "{\"message\":\"Hello, Learner! Your API dev setup is working.\"}")
        )
    )
    @GetMapping("/api/v1/greetings/{name}")
    public Map<String, String> greet(
        @Parameter(description = "The name to greet.", required = true)
        @PathVariable String name
    ) {
        return Map.of("message", "Hello, " + name + "! Your API dev setup is working.");
    }
}
