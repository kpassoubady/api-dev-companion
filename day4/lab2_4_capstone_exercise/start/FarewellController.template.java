// Reference template for a NEW file to create at:
//   api-dev-setup/quickstart-project/src/main/java/com/apidev/quickstart/controller/FarewellController.java
//
// Unlike Lab 1.3's templates, this is not an edit to an existing controller.
// Copy this whole file (with your TODO block filled in) to the path above,
// alongside the existing GreetingController.java and HealthController.java.
// Do not create a second FarewellController; there must be exactly one, in
// the setup repo.

package com.apidev.quickstart.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// TODO 1: Add the springdoc-openapi imports you used on GreetingController in
//   Lab 1.3: io.swagger.v3.oas.annotations.Operation,
//   io.swagger.v3.oas.annotations.Parameter, and
//   io.swagger.v3.oas.annotations.responses.ApiResponse. The dependency is
//   already on the classpath (springdoc-openapi-starter-webmvc-ui in
//   pom.xml) — nothing new to install.

@RestController
public class FarewellController {

    // TODO 1 (continued): Add @Operation(summary = "...", description = "...")
    //   and @ApiResponse(responseCode = "200", description = "...") directly
    //   above the method below — the same pattern you applied to
    //   GreetingController.greet() in Lab 1.3. Also add
    //   @Parameter(description = "...", required = true) to the `name`
    //   parameter below. Everything else here — the mapping, the path
    //   variable, the response body — is already correct; you are only
    //   adding documentation.
    @GetMapping("/api/v1/farewells/{name}")
    public Map<String, String> farewell(@PathVariable String name) {
        return Map.of("message", "Goodbye, " + name + "! See you in the next session.");
    }
}
