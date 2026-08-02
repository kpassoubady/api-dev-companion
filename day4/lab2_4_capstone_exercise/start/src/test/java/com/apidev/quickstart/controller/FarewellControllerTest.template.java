// Reference template for a NEW file to create at:
//   api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/FarewellControllerTest.java
//
// Follows the same MockMvc + JUnit5 pattern already used by
// GreetingControllerTest.java and HealthControllerTest.java in that same
// directory. Copy this whole file to the path above once FarewellController
// exists there (Step 2 of this lab), then fill in the two TODOs.

package com.apidev.quickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FarewellControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO 1: Happy path.
    //   Perform a GET on /api/v1/farewells/Learner. Assert status().isOk()
    //   and that $.message equals
    //   "Goodbye, Learner! See you in the next session."
    //   Model this on GreetingControllerTest's
    //   greetingEndpointReturnsPersonalizedMessage() test in the same
    //   package.
    @Test
    void farewellEndpointReturnsPersonalizedMessage() throws Exception {
        // TODO 1: implement
    }

    // TODO 2: Negative / edge case.
    //   This endpoint only maps GET. Perform a POST to the same path
    //   (/api/v1/farewells/Learner) and assert status().isMethodNotAllowed()
    //   (405) — the same check Lab 1.1 asked you to make by hand against
    //   POST /api/v1/health in Part 3 of its worksheet, now written as a
    //   repeatable test instead of a one-time observation.
    @Test
    void farewellEndpointRejectsUnsupportedMethod() throws Exception {
        // TODO 2: implement
    }
}
