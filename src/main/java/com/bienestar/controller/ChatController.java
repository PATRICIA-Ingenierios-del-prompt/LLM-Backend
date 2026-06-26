package com.bienestar.controller;

import com.bienestar.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:3000" })
public class ChatController {

    private final LlmService llmService;

    public ChatController(LlmService llmService) {
        this.llmService = llmService;
    }

    /**
     * POST /api/chat
     * Body: { "message": "texto del usuario" }
     * Returns: { "response": "respuesta del LLM" }
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.getOrDefault("message", "").trim();
        if (userMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El mensaje no puede estar vacío."));
        }
        String response = llmService.getChatbotResponse(userMessage);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * POST /api/diary
     * Body: { "mood": "Feliz", "content": "texto del diario" }
     * Returns: { "response": "consejo del LLM" }
     */
    @PostMapping("/diary")
    public ResponseEntity<Map<String, String>> diary(@RequestBody Map<String, String> body) {
        String mood    = body.getOrDefault("mood", "").trim();
        String content = body.getOrDefault("content", "").trim();
        if (content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El contenido no puede estar vacío."));
        }
        String advice = llmService.getDiaryAdvice(mood, content);
        return ResponseEntity.ok(Map.of("response", advice));
    }

    /**
     * GET /api/health
     * Quick health-check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "Bienestar Estudiantil LLM API"));
    }
}
