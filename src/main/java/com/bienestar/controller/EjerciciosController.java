package com.bienestar.controller;

import com.bienestar.entity.ExerciseCompletion;
import com.bienestar.model.ExerciseType;
import com.bienestar.service.ExerciseCompletionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bienestar")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://localhost:8080" })
public class EjerciciosController {

    private final ExerciseCompletionService exerciseCompletionService;

    public EjerciciosController(ExerciseCompletionService exerciseCompletionService) {
        this.exerciseCompletionService = exerciseCompletionService;
    }

    /**
     * POST /api/bienestar/ejercicios
     * Header: X-User-Id (inyectado por el Gateway a partir del JWT)
     * Body: { "tipo": "RESPIRACION_478" | "BOX_BREATHING" }
     */
    @PostMapping("/ejercicios")
    public ResponseEntity<?> completarEjercicio(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "Falta X-User-Id (la peticion debe pasar por el Gateway)."));
        }

        String tipoRaw = body.getOrDefault("tipo", "").trim().toUpperCase();
        ExerciseType tipo;
        try {
            tipo = ExerciseType.valueOf(tipoRaw);
        } catch (IllegalArgumentException e) {
            String validos = Arrays.stream(ExerciseType.values()).map(Enum::name).collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(Map.of("error", "tipo invalido. Valores validos: " + validos));
        }

        ExerciseCompletion saved = exerciseCompletionService.registerCompletion(userId, tipo);
        return ResponseEntity.status(201).body(saved);
    }

    /**
     * GET /api/bienestar/usuarios/{id}/ejercicios/count
     */
    @GetMapping("/usuarios/{id}/ejercicios/count")
    public ResponseEntity<Map<String, Object>> contarEjercicios(@PathVariable("id") String id) {
        long total = exerciseCompletionService.countCompletions(id);
        return ResponseEntity.ok(Map.of("userId", id, "total", total));
    }
}
