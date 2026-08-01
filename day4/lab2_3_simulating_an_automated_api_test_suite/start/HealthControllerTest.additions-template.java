// Reference template for extending the REAL file at:
//   api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/HealthControllerTest.java
//
// The real file already has healthEndpointReportsUp() (the one happy-path
// test from the installation-verification quickstart). This template shows
// that same test, a new worked-example negative-path test, and two TODOs.
// Copy the new methods below onto the real file. Do not create a second
// HealthControllerTest; there must be exactly one, in the setup repo.

package com.apidev.quickstart.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReportsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("api-dev-quickstart"));
    }

    /**
     * Worked example: negative-path test. Lab 1.1's worksheet recorded that
     * an unmapped path does not come back as a 200 with an empty body, it
     * comes back as a 404. This turns that one-time manual observation into
     * a repeatable assertion.
     */
    @Test
    void unmappedPathReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/does-not-exist"))
            .andExpect(status().isNotFound());
    }

    /**
     * TODO 1: Lab 1.1's worksheet (Part 5/6) recorded that POST to
     * /api/v1/health returns 405 (Method Not Allowed), and that the
     * response carries an "Allow" header naming the supported method.
     * Assert both: the 405 status, and that the "Allow" header contains
     * "GET". (containsString is already imported for you above.)
     */
    @Test
    void postToHealthReturns405WithAllowHeader() throws Exception {
        // TODO 1: implement
    }

    /**
     * TODO 2: Lab 1.1's worksheet also recorded that every 2xx response
     * carries Content-Type: application/json. Assert that GET
     * /api/v1/health returns that content type. Use
     * content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
     * rather than contentType(...) so the assertion still passes if the
     * server adds a charset parameter you didn't ask about.
     */
    @Test
    void healthResponseContentTypeIsJson() throws Exception {
        // TODO 2: implement
    }
}
