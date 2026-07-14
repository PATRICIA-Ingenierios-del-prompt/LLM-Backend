package com.bienestar.service;

import com.bienestar.entity.ExerciseCompletion;
import com.bienestar.model.ExerciseType;
import com.bienestar.repository.ExerciseCompletionRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseCompletionService {

    private static final String CATEGORIA_RELAJACION = "RELAJACION";

    private final ExerciseCompletionRepository exerciseCompletionRepository;

    public ExerciseCompletionService(ExerciseCompletionRepository exerciseCompletionRepository) {
        this.exerciseCompletionRepository = exerciseCompletionRepository;
    }

    public ExerciseCompletion registerCompletion(String userId, ExerciseType tipo) {
        ExerciseCompletion entry = new ExerciseCompletion(userId, tipo.name(), CATEGORIA_RELAJACION);
        return exerciseCompletionRepository.save(entry);
    }

    public long countCompletions(String userId) {
        return exerciseCompletionRepository.countByUserId(userId);
    }
}
