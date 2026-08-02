// Reference solution: the REAL file at
//   api-dev-setup/quickstart-project/src/test/java/com/apidev/quickstart/controller/GreetingControllerTest.java
// after completing Lab 2.3. Verified with `mvn test` against the shared
// quickstart-project.

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

    @Test
    void greetingHandlesNameWithSpaces() throws Exception {
        mockMvc.perform(get("/api/v1/greetings/{name}", "Ada Lovelace"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, Ada Lovelace! Your API dev setup is working."));
    }

    @Test
    void greetingWithoutNameReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/greetings/"))
            .andExpect(status().isNotFound());
    }
}
