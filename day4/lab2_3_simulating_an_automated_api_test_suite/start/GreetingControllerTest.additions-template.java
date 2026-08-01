// Reference template for extending the REAL file at:
//   api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/GreetingControllerTest.java
//
// The real file already has greetingEndpointReturnsPersonalizedMessage() (the
// one happy-path test from the installation-verification quickstart). This
// template adds two TODOs. Copy the new methods below onto the real file.
// Do not create a second GreetingControllerTest; there must be exactly one,
// in the setup repo.

package com.apidev.quickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void greetingEndpointReturnsPersonalizedMessage() throws Exception {
        mockMvc.perform(get("/api/v1/greetings/Learner"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, Learner! Your API dev setup is working."));
    }

    /**
     * TODO 3: The controller reflects whatever name it is given, with no
     * validation of its own. Prove that it handles an unusual-but-legal
     * name sensibly: one containing a space. Pass the name as a URI
     * template variable, get("/api/v1/greetings/{name}", "Ada Lovelace"),
     * rather than hand-encoding the space yourself — that avoids a
     * double-encoding bug where "%20" would appear literally in the name.
     * Expect a 200 and a message of
     * "Hello, Ada Lovelace! Your API dev setup is working."
     */
    @Test
    void greetingHandlesNameWithSpaces() throws Exception {
        // TODO 3: implement
    }

    /**
     * TODO 4: A path variable segment cannot be empty. Prove that
     * requesting /api/v1/greetings/ with no name after the trailing slash
     * returns 404, not a 200 with a blank name and not a 500. This is the
     * same "what actually comes back" discipline Lab 1.1 used on every
     * endpoint, pointed at a case the current test suite doesn't cover.
     */
    @Test
    void greetingWithoutNameReturns404() throws Exception {
        // TODO 4: implement
    }
}
