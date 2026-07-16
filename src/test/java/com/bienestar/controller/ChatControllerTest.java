package com.bienestar.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.main.banner-mode=off"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/bienestar/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void chatEndpointReturnsBadRequestWhenMessageIsEmpty() throws Exception {
        mockMvc.perform(post("/api/bienestar/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void diaryEndpointReturnsBadRequestWhenContentIsEmpty() throws Exception {
        mockMvc.perform(post("/api/bienestar/diary")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mood\": \"Feliz\", \"content\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void diaryEndpointReturnsUnauthorizedWhenUserIdHeaderMissing() throws Exception {
        mockMvc.perform(post("/api/bienestar/diary")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mood\": \"Feliz\", \"content\": \"Hoy fue un buen día\"}"))
                .andExpect(status().isUnauthorized());
    }
}
