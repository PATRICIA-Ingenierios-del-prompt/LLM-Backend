package com.bienestar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.main.banner-mode=off"
})
@ActiveProfiles("test")
class LlmServiceTest {

    @Autowired
    private LlmService llmService;

    @Test
    void testInitAndResponses() {
        assertNotNull(llmService);
        // We only check that the service is initialized properly and the context loads without crashing
        // Actual LLM call might fail or succeed depending on API Key, but it should return a non-null string in any case
        String response = llmService.getChatbotResponse("Hola");
        assertNotNull(response);
    }
}
