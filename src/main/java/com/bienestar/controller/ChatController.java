package com.bienestar.controller;

import com.bienestar.service.DiaryRepository;
import com.bienestar.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bienestar")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://localhost:8080" })
public class ChatController {

    private final LlmService llmService;
    private final DiaryRepository diaryRepository;

    public ChatController(LlmService llmService, DiaryRepository diaryRepository) {
        this.llmService = llmService;
        this.diaryRepository = diaryRepository;
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
     * Header: X-User-Id (inyectado por el Gateway a partir del JWT)
     * Body: { "mood": "Feliz", "content": "texto del diario" }
     * Returns: { "response": "consejo del LLM" }
     */
    @PostMapping("/diary")
    public ResponseEntity<Map<String, String>> diary(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        String mood    = body.getOrDefault("mood", "").trim();
        String content = body.getOrDefault("content", "").trim();
        if (content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El contenido no puede estar vacío."));
        }
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "Falta X-User-Id (la petición debe pasar por el Gateway)."));
        }
        diaryRepository.saveEntry(userId, content, mood);
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
