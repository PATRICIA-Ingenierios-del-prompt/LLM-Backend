package com.bienestar.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Contributes to the readiness probe group (see application.yml:
 * management.endpoint.health.group.readiness.include=readinessState,wellbeing).
 *
 * The pod is only marked READY when the LLM assistant is actually wired
 * (chat model + local embeddings initialized). If GROQ_API_KEY is missing or
 * invalid, LlmService leaves the assistant null and this reports DOWN, so the
 * rollout fails loudly at deploy time instead of shipping a broken chatbot
 * that returns HTTP 200 "servicio no configurado".
 *
 * Named "wellbeing" so the health group id matches the microservice name.
 */
@Component("wellbeing")
public class WellbeingHealthIndicator implements HealthIndicator {

    private final LlmService llmService;

    public WellbeingHealthIndicator(LlmService llmService) {
        this.llmService = llmService;
    }

    @Override
    public Health health() {
        if (llmService.isReady()) {
            return Health.up().withDetail("llm", "assistant ready").build();
        }
        return Health.down().withDetail("llm", "assistant not configured (check GROQ_API_KEY)").build();
    }
}
