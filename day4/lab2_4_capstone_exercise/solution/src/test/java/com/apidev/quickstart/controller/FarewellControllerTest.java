// Reference solution: the REAL file at
//   api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/FarewellControllerTest.java
// after completing this lab.

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

    @Test
    void farewellEndpointReturnsPersonalizedMessage() throws Exception {
        mockMvc.perform(get("/api/v1/farewells/Learner"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Goodbye, Learner! See you in the next session."));
    }

    @Test
    void farewellEndpointRejectsUnsupportedMethod() throws Exception {
        mockMvc.perform(post("/api/v1/farewells/Learner"))
            .andExpect(status().isMethodNotAllowed());
    }
}
